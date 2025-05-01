"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.launch = void 0;
const apiCalls_1 = require("../../apiCalls");
const fs_1 = require("fs");
const constants_1 = require("../../constants");
const aal_1 = require("../../lib/aal");
const path = require('path');
const log = require('electron-log').scope('launch');
async function launch(programArgs, allocatedMemoryMb, version, branch, callbacks) {
    try {
        const contents = JSON.stringify({ heap: allocatedMemoryMb });
        await fs_1.promises.mkdir(constants_1.AAL_DIRECTORY, { recursive: true });
        await fs_1.promises.writeFile(path.join(constants_1.AAL_DIRECTORY, 'AAL.config.json'), contents);
    }
    catch (err) {
        log.error(err);
    }
    let lastStatusMessage = "Connecting"; // Last status message, for faster rendering
    let statusMessage = "Connecting"; // Current status message to set
    let lastStatusMessageError = false; // Was last status an error?
    let statusMessageError = false; // Is current status message an error?
    let dl_total = 1; // AAL src download size
    const response = await apiCalls_1.makeLaunchRequest(version, branch, apiCalls_1.LaunchType.ALPHA_ANTI_LEAK, {});
    callbacks.progress('Connecting', 'Contacting AC servers...');
    return new Promise((resolve, reject) => {
        let opened = false;
        let outputcb = (output) => {
            if (!output)
                return;
            callbacks.log(output);
            // one time latch
            if (output.includes('LUNARCLIENT_STATUS_INIT')) {
                opened = true;
            }
            if (!opened) {
                // AAL output parsing
                output = output.replace("\r", "");
                // AAL Launcher messages
                if (output.indexOf("AAL_LAUNCHER_STATUS_") !== -1) {
                    if (output === "AAL_LAUNCHER_STATUS_ERROR_VERSION_OUTDATED") {
                        statusMessage = "AAL Launcher Core is out of date. Please relaunch";
                    }
                    else if (output === "AAL_LAUNCHER_STATUS_CHECKING_FOR_UPDATES") {
                        statusMessage = "Checking for AAL updates";
                    }
                    else if (output.indexOf("AAL_LAUNCHER_STATUS_DOWNLOAD_") !== -1) {
                        let params = output.substr("AAL_LAUNCHER_STATUS_DOWNLOAD_".length);
                        let paramsSplit = params.split("_");
                        let dl_launcher_total = parseInt(paramsSplit[0], 10);
                        let dl_launcher_cur = parseInt(paramsSplit[1], 10);
                        if (dl_launcher_cur !== dl_launcher_total) {
                            statusMessage = "Downloading AAL Update: " + ((dl_launcher_cur / dl_launcher_total) * 100.0).toFixed(1) + "%";
                        }
                        else {
                            statusMessage = "Extracting AAL Update";
                        }
                    }
                    else if (output === "AAL_LAUNCHER_STATUS_UP_TO_DATE") {
                        statusMessage = "AAL Update downloaded";
                    }
                    else if (output.indexOf("AAL_LAUNCHER_STATUS_ERROR_INCOMPATIBLE_SOFTWARE_") !== -1) {
                        statusMessage = "AAL Incompatible Software detected: " + output.substr("AAL_LAUNCHER_STATUS_ERROR_INCOMPATIBLE_SOFTWARE_".length) + ". Please uninstall the software and try again!";
                        statusMessageError = true;
                    }
                    else if (output.indexOf("AAL_LAUNCHER_STATUS_EXIT_") !== -1) {
                        let exitCode = output.substr("AAL_LAUNCHER_STATUS_EXIT_".length);
                        if (statusMessage.indexOf("Error: ") === -1)
                            statusMessage = "AAL Process exited: " + exitCode;
                        statusMessageError = true;
                    }
                    else if (output.indexOf("AAL_LAUNCHER_STATUS_ERROR_") !== -1) {
                        let err = output.substr("AAL_LAUNCHER_STATUS_ERROR_".length);
                        statusMessage = "AAL Launcher Error: " + err;
                        statusMessageError = true;
                    }
                }
                else if (output.indexOf("AAL_STATUS_") !== -1) { // AAL core messages
                    if (output.indexOf("AAL_STATUS_DOWNLOAD_SIZE_") !== -1) {
                        dl_total = parseInt(output.substr("AAL_STATUS_DOWNLOAD_SIZE_".length), 10);
                        if (dl_total > 0) {
                            statusMessage = "Downloading Lunar Client: 0%";
                        }
                    }
                    else if (output.indexOf("AAL_STATUS_DOWNLOAD_REMAINING_") !== -1) {
                        let dl_cur = dl_total - parseInt(output.substr("AAL_STATUS_DOWNLOAD_REMAINING_".length), 10);
                        statusMessage = "Downloading Lunar Client: " + ((dl_cur / dl_total) * 100.0).toFixed(1) + "%";
                    }
                    else if (output === "AAL_STATUS_LOGIN_SUCCESS") {
                        statusMessage = "AAL: Logged in. Launching...";
                    }
                    else if (output === "AAL_STATUS_CONNECTED") {
                        statusMessage = "AAL: Connected. Logging in...";
                    }
                    else if (output === "AAL_STATUS_INITIALIZE") {
                        statusMessage = "Initializing AAL";
                    }
                    else if (output === "AAL_STATUS_SERVER_CONNECTION_ERROR") {
                        statusMessage = "AAL Error: Failed connecting";
                    }
                    else if (output.indexOf("AAL_STATUS_EXCEPTION_") !== -1) {
                        let errorMsg = output.substr("AAL_STATUS_EXCEPTION_".length);
                        statusMessage = "AAL Error: " + errorMsg;
                        statusMessageError = true;
                    }
                    else if (output === "AAL_STATUS_LOGIN_FAILED") {
                        statusMessage = "AAL Error: Login failed";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_STATUS_LOGIN_ERROR") {
                        statusMessage = "AAL Error: A server-side error occurred during login";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_STATUS_LAUNCH_SERVER_SIDE_ERROR") {
                        statusMessage = "AAL Error: A server-side error occurred while launching";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_STATUS_LAUNCH_NO_PERMISSIONS") {
                        statusMessage = "AAL Error: You do not have permissions to launch this application";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_STATUS_LAUNCH_OS_DISABLED") {
                        statusMessage = "AAL Error: The owner of this app blocked your OS";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_STATUS_LAUNCH_UNAUTHORIZED_HWID") {
                        statusMessage = "AAL Error: Your Hardware Identification is unauthorized. Contact your app provider";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_STATUS_LAUNCH_UNKNOWN_APP") {
                        statusMessage = "AAL Error: The app your trying to launch doesn't exist";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_STATUS_LAUNCH_APP_NOT_CONFIGURED") {
                        statusMessage = "AAL Error: Lunar Client AAL configuration error";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_STATUS_LAUNCH_UNKNOWN_SERVER_SIDE_ERROR") {
                        statusMessage = "AAL Error: AAL: An unknown server-side error occurred";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_STATUS_LAUNCH_MAIL_UNVERIFIED") {
                        statusMessage = "AAL Error: Your E-Mail isn't verified";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_STATUS_SESSION_EXPIRED") {
                        statusMessage = "AAL Error: Session expired, please relaunch";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_STATUS_WINE_DETECTED") {
                        statusMessage = "AAL Error: Wine detected. Do not use Wine.";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_STATUS_WIN_COMPATIBLITY_MODE_DETECTED") {
                        statusMessage = "AAL Error: Windows compatibility mode detected. Please turn off Windows compatibility mode.";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_STATUS_SANDBOX_DETECTED") {
                        statusMessage = "AAL Error: Sandbox detected. Do not use Sandboxes.";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_LAUNCHER_ERROR_TESTMODE") {
                        statusMessage = "AAL: Windows Test mode detected. Please turn it off and try again.";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_LAUNCHER_ERROR_KERNEL_DBG") {
                        statusMessage = "AAL: Kernel Debugger detected. Please remove it and try again.";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_LAUNCHER_ERROR_APPINIT") {
                        statusMessage = "AAL: Windows AppInit DLLs detection failure. Please contact support@alphaantileak.net for assistance.";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_LAUNCHER_ERROR_OUTDATED_OS") {
                        statusMessage = "Your Operating System is too old for AAL. Windows 7 with Service Pack 1 is required.";
                        statusMessageError = true;
                    }
                    else if (output === "AAL_STATUS_LAUNCH_BANNED") {
                        statusMessage = "You're still banned";
                        statusMessageError = true;
                    }
                }
                if (lastStatusMessage !== statusMessage && !lastStatusMessageError) {
                    if (statusMessageError) {
                        reject({
                            short: statusMessage,
                            description: 'Failed to start anticheat'
                        });
                    }
                    else {
                        callbacks.progress('Launching', statusMessage);
                    }
                    lastStatusMessage = statusMessage;
                    lastStatusMessageError = statusMessageError;
                }
            }
        };
        aal_1.launch(response.app, response.session, programArgs, callbacks.exit, outputcb);
        resolve();
    });
}
exports.launch = launch;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiYWxwaGFhbnRpbGVhay5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3NyYy9qcy9sYXVuY2gvdHlwZXMvYWxwaGFhbnRpbGVhay50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFBQSw2Q0FBd0U7QUFFeEUsMkJBQW9DO0FBQ3BDLCtDQUFnRDtBQUNoRCx1Q0FBb0Q7QUFDcEQsTUFBTSxJQUFJLEdBQUcsT0FBTyxDQUFDLE1BQU0sQ0FBQyxDQUFDO0FBQzdCLE1BQU0sR0FBRyxHQUFHLE9BQU8sQ0FBQyxjQUFjLENBQUMsQ0FBQyxLQUFLLENBQUMsUUFBUSxDQUFDLENBQUM7QUFFN0MsS0FBSyxVQUFVLE1BQU0sQ0FBQyxXQUEwQixFQUFFLGlCQUF5QixFQUFFLE9BQWdCLEVBQUUsTUFBYyxFQUFFLFNBQTBCO0lBQzVJLElBQUk7UUFDQSxNQUFNLFFBQVEsR0FBRyxJQUFJLENBQUMsU0FBUyxDQUFDLEVBQUUsSUFBSSxFQUFFLGlCQUFpQixFQUFFLENBQUMsQ0FBQztRQUM3RCxNQUFNLGFBQUUsQ0FBQyxLQUFLLENBQUMseUJBQWEsRUFBRSxFQUFFLFNBQVMsRUFBRSxJQUFJLEVBQUUsQ0FBQyxDQUFDO1FBQ25ELE1BQU0sYUFBRSxDQUFDLFNBQVMsQ0FBQyxJQUFJLENBQUMsSUFBSSxDQUFDLHlCQUFhLEVBQUUsaUJBQWlCLENBQUMsRUFBRSxRQUFRLENBQUMsQ0FBQztLQUM3RTtJQUFDLE9BQU8sR0FBRyxFQUFFO1FBQ1YsR0FBRyxDQUFDLEtBQUssQ0FBQyxHQUFHLENBQUMsQ0FBQztLQUNsQjtJQUVELElBQUksaUJBQWlCLEdBQUcsWUFBWSxDQUFDLENBQUMsNENBQTRDO0lBQ2xGLElBQUksYUFBYSxHQUFHLFlBQVksQ0FBQyxDQUFDLGdDQUFnQztJQUNsRSxJQUFJLHNCQUFzQixHQUFHLEtBQUssQ0FBQyxDQUFDLDRCQUE0QjtJQUNoRSxJQUFJLGtCQUFrQixHQUFHLEtBQUssQ0FBQyxDQUFDLHNDQUFzQztJQUV0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