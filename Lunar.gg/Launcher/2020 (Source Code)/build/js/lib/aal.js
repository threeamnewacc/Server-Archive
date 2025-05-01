"use strict";
// AlphaAntiLeak NodeJS Runner Module
const fs = require("fs");
const crypto = require("crypto");
const http = require("http");
const https = require("https");
const { spawn } = require("child_process");
const log = require('electron-log').scope('aal');
const aalcdn = "https://cdn.alphaantileak.net";
/**
 * Hashes a file
 * @param path The path to the file
 * @returns {Buffer} The base64 encoded sha1
 */
function hashFile(path) {
    let data = fs.readFileSync(path);
    let hash = crypto.createHash("sha256");
    hash.update(data);
    return hash.digest();
}
/**
 * Gets the installation package
 * @returns {string} When OS is supported return identifier, otherwise null
 */
function getLauncherFile() {
    if (process.platform === "win32") {
        return "AAL_Windows_Launcher";
    }
    return null;
}
function ___validateInstall(resolve, reject, url, local_hash) {
    let lib = url.startsWith("https:") ? https : http;
    lib.get(url, res => {
        if (res.statusCode >= 300 && res.statusCode < 400 && res.headers["location"]) {
            url = res.headers["location"];
            res.resume();
            ___validateInstall(resolve, reject, url, local_hash);
            return;
        }
        if (res.statusCode !== 200) {
            res.resume(); // free memory
            reject(`A server-side ${res.statusCode} error occurred`);
            return;
        }
        let rawData = Buffer.alloc(0);
        res.on("data", chunk => {
            let new_buf = Buffer.from(chunk);
            rawData = Buffer.concat([rawData, new_buf], rawData.length + new_buf.length);
        });
        res.on("end", () => {
            resolve(rawData.equals(local_hash));
        });
    }).on("error", e => {
        log.info("[Native] Error validating Installation", e);
        reject(e);
    });
}
function __validateInstall(resolve, reject) {
    // Get install package
    let launcher_file = getLauncherFile();
    if (!launcher_file) {
        reject("Unsupported OS");
        return;
    }
    let local_hash;
    try {
        local_hash = hashFile(launcher_file + getPlatformExecutableExt());
    }
    catch (e) {
        console.error("Failed hashing file ", e);
        resolve(false); // due to this most likely being invalid hash
        return;
    }
    ___validateInstall(resolve, reject, aalcdn + "/AAL/" + launcher_file + ".hash", local_hash);
}
/**
 * Validates the installation
 * @returns {Promise<boolean>} Is installation valid
 */
function validateInstall() {
    return new Promise(__validateInstall);
}
function __install_download(url, launcher_file, resolve, reject, status_callback) {
    let lib = url.startsWith("https:") ? https : http;
    lib.get(url, res => {
        log.info("[Native] Downloading...");
        if (res.statusCode >= 300 && res.statusCode < 400 && res.headers["location"]) {
            url = res.headers["location"];
            res.resume();
            __install_download(url, launcher_file, resolve, reject, status_callback);
            return;
        }
        if (res.statusCode !== 200) {
            res.on("data", chunk => {
                log.info(chunk.toString('utf-8'));
            });
            reject("A server-side " + res.statusCode + " error occurred");
            return;
        }
        try {
            fs.chmodSync(launcher_file, 0o774); // make writable
        }
        catch (e) { }
        let out = fs.createWriteStream(launcher_file);
        let dataRead = 0;
        let finalLen = res.headers["content-length"];
        res.on("data", chunk => {
            dataRead += chunk.length;
            status_callback(dataRead / finalLen);
            out.write(chunk);
        });
        res.on("end", () => {
            status_callback(1.0);
            out.on("close", () => {
                fs.chmodSync(launcher_file, 0o774);
                resolve();
            });
            out.end();
        });
    }).on("error", e => {
        log.info("[Native] Error downloading", e);
        reject(e);
    });
}
/**
 * Installs the AAL Launcher Core
 * @param status_callback a callback which receives a percentage (0.0 - 1.0)
 * @returns {Promise<void>} a callback
 */
function install(status_callback) {
    if (typeof status_callback !== "function") {
        status_callback = function (percent) { };
    }
    return new Promise(function (resolve, reject) {
        status_callback(0.0);
        log.info("Installing...");
        let launcher_file = getLauncherFile() + getPlatformExecutableExt();
        if (!launcher_file) {
            reject("Unsupported OS");
            return;
        }
        let url = aalcdn + "/AAL/" + launcher_file;
        __install_download(url, launcher_file, resolve, reject, status_callback);
    });
}
function getPlatformExecutableExt() {
    if (process.platform === "win32") {
        return ".exe";
    }
    else if (process.platform === "linux") {
        return "";
    }
    return null;
}
function startAAL(appid, session, args, donecb, outputcb, processcb) {
    log.info("Starting native");
    let finalArgs = [appid, session];
    let exe = getLauncherFile() + getPlatformExecutableExt();
    args.forEach(arg => finalArgs.push(arg));
    if (process.platform !== "win32") { // unix system
        fs.chmodSync(exe, 0o774);
    }
    console.debug("Native Pre Launch");
    let AAL = spawn(exe, finalArgs, { windowsHide: true, detached: true });
    console.debug("AAL Post Launch");
    AAL.stdout.on("data", outputcb);
    AAL.stderr.on("data", outputcb);
    AAL.on("exit", code => {
        log.info("AAL-Core exited with code " + code);
        donecb(code);
    });
    processcb(AAL);
}
let install_lock = false;
function __ensureInstallation(status_callback, resolve, reject) {
    validateInstall().then(valid => {
        if (valid)
            resolve();
        else
            install(status_callback).then(resolve).catch(reject);
    }).catch(reject);
}
function _ensureInstallation(status_callback, resolve, reject) {
    if (install_lock) {
        setTimeout(_ensureInstallation, 1000, status_callback, resolve, reject);
    }
    else {
        install_lock = true;
        let resolveHook = function (ret) {
            install_lock = false;
            resolve(ret);
        };
        let rejectHook = function (err) {
            install_lock = false;
            reject(err);
        };
        __ensureInstallation(status_callback, resolveHook, rejectHook);
    }
}
function ensureInstallation(status_callback) {
    return new Promise(function (resolve, reject) {
        _ensureInstallation(status_callback, resolve, reject);
    });
}
function launchApp(appid, session, args, errorcb, donecb, outputcb, processcb) {
    ensureInstallation().then(() => {
        const output_handler = data => {
            // turn windows and mac os output into linux output
            data = data.toString().replace("\r\n", "\n").replace("\r", "\n");
            data.split("\n").forEach(line => {
                outputcb(line);
            });
        };
        startAAL(appid, session, args, donecb, output_handler, processcb);
    }).catch(errorcb);
}
function launch(appid, session, args, donecb, outputcb) {
    launchApp(appid, session, args, outputcb, donecb, outputcb, p => { });
}
function isCompatibleWithSystem() {
    return process.platform === 'win32' && process.env.PROCESSOR_ARCHITECTURE === 'AMD64';
}
module.exports = {
    "launch": launch,
    "isCompatibleWithSystem": isCompatibleWithSystem
};
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiYWFsLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL2pzL2xpYi9hYWwuanMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IjtBQUFBLHFDQUFxQztBQUVyQyxNQUFNLEVBQUUsR0FBRyxPQUFPLENBQUMsSUFBSSxDQUFDLENBQUM7QUFDekIsTUFBTSxNQUFNLEdBQUcsT0FBTyxDQUFDLFFBQVEsQ0FBQyxDQUFDO0FBQ2pDLE1BQU0sSUFBSSxHQUFHLE9BQU8sQ0FBQyxNQUFNLENBQUMsQ0FBQztBQUM3QixNQUFNLEtBQUssR0FBRyxPQUFPLENBQUMsT0FBTyxDQUFDLENBQUM7QUFDL0IsTUFBTSxFQUFFLEtBQUssRUFBRSxHQUFHLE9BQU8sQ0FBQyxlQUFlLENBQUMsQ0FBQztBQUMzQyxNQUFNLEdBQUcsR0FBRyxPQUFPLENBQUMsY0FBYyxDQUFDLENBQUMsS0FBSyxDQUFDLEtBQUssQ0FBQyxDQUFDO0FBQ2pELE1BQU0sTUFBTSxHQUFHLCtCQUErQixDQUFDO0FBRy9DOzs7O0dBSUc7QUFDSCxTQUFTLFFBQVEsQ0FBQyxJQUFJO0lBQ3JCLElBQUksSUFBSSxHQUFHLEVBQUUsQ0FBQyxZQUFZLENBQUMsSUFBSSxDQUFDLENBQUM7SUFDakMsSUFBSSxJQUFJLEdBQUcsTUFBTSxDQUFDLFVBQVUsQ0FBQyxRQUFRLENBQUMsQ0FBQztJQUN2QyxJQUFJLENBQUMsTUFBTSxDQUFDLElBQUksQ0FBQyxDQUFDO0lBQ2xCLE9BQU8sSUFBSSxDQUFDLE1BQU0sRUFBRSxDQUFDO0FBQ3RCLENBQUM7QUFFRDs7O0dBR0c7QUFDSCxTQUFTLGVBQWU7SUFDdkIsSUFBSSxPQUFPLENBQUMsUUFBUSxLQUFLLE9BQU8sRUFBRTtRQUNqQyxPQUFPLHNCQUFzQixDQUFBO0tBQzdCO0lBQ0QsT0FBTyxJQUFJLENBQUM7QUFDYixDQUFDO0FBRUQsU0FBUyxrQkFBa0IsQ0FBQyxPQUFPLEVBQUUsTUFBTSxFQUFFLEdBQUcsRUFBRSxVQUFVO0lBQ3hELElBQUks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QUMsQ0FBQztRQUNQLENBQUMsQ0FBQztRQUVGLFFBQVEsQ0FBQyxLQUFLLEVBQUUsT0FBTyxFQUFFLElBQUksRUFBRSxNQUFNLEVBQUUsY0FBYyxFQUFFLFNBQVMsQ0FBQyxDQUFDO0lBQ3RFLENBQUMsQ0FBQyxDQUFDLEtBQUssQ0FBQyxPQUFPLENBQUMsQ0FBQztBQUN0QixDQUFDO0FBRUQsU0FBUyxNQUFNLENBQUMsS0FBSyxFQUFFLE9BQU8sRUFBRSxJQUFJLEVBQUUsTUFBTSxFQUFFLFFBQVE7SUFDckQsU0FBUyxDQUFDLEtBQUssRUFBRSxPQUFPLEVBQUUsSUFBSSxFQUFFLFFBQVEsRUFBRSxNQUFNLEVBQUUsUUFBUSxFQUFFLENBQUMsQ0FBQyxFQUFFLEdBQUUsQ0FBQyxDQUFDLENBQUM7QUFDdEUsQ0FBQztBQUVELFNBQVMsc0JBQXNCO0lBQzNCLE9BQU8sT0FBTyxDQUFDLFFBQVEsS0FBSyxPQUFPLElBQUksT0FBTyxDQUFDLEdBQUcsQ0FBQyxzQkFBc0IsS0FBSyxPQUFPLENBQUM7QUFDMUYsQ0FBQztBQUVELE1BQU0sQ0FBQyxPQUFPLEdBQUc7SUFDaEIsUUFBUSxFQUFFLE1BQU07SUFDYix3QkFBd0IsRUFBRSxzQkFBc0I7Q0FDbkQsQ0FBQyJ9