"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.joinClasspath = exports.getJREExecutable = exports.ensureJREInstalled = void 0;
const fs_1 = require("fs");
const path_1 = require("path");
const jre_1 = require("../lib/jre");
async function ensureJREInstalled(progressCallback) {
    const dirExists = fs_1.existsSync(jre_1.jreDir());
    let needsInstall;
    if (dirExists) {
        const check = [
            jre_1.driver(),
            path_1.join(jre_1.driver(), '..', '..', 'lib', 'rt.jar'),
            path_1.join(jre_1.driver(), '..', '..', 'Welcome.html'),
        ];
        // file doesn't exist on macOS JRE
        if (process.platform !== 'darwin') {
            check.push(path_1.join(jre_1.driver(), '..', '..', 'lib', 'amd64', 'jvm.cfg'));
        }
        try {
            const allExist = check.every(fs_1.existsSync);
            needsInstall = !allExist;
        }
        catch (err) {
            needsInstall = true;
        }
    }
    else {
        needsInstall = true;
    }
    if (needsInstall) {
        return new Promise((resolve, _reject) => {
            jre_1.install(resolve, (progress) => {
                progressCallback('Updating', 'Downloading JVM: ' + (progress * 100.0).toFixed(1) + '%');
            });
        });
    }
}
exports.ensureJREInstalled = ensureJREInstalled;
function getJREExecutable() {
    return jre_1.driver();
}
exports.getJREExecutable = getJREExecutable;
function joinClasspath(entries) {
    // @ts-ignore
    return entries.join(jre_1.platform() === 'windows' ? ';' : ':');
}
exports.joinClasspath = joinClasspath;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoianJlLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL2pzL2xhdW5jaC9qcmUudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsMkJBQWdDO0FBQ2hDLCtCQUE0QjtBQUM1QixvQ0FBK0Q7QUFHeEQsS0FBSyxVQUFVLGtCQUFrQixDQUFDLGdCQUF3QztJQUM3RSxNQUFNLFNBQVMsR0FBRyxlQUFVLENBQUMsWUFBTSxFQUFFLENBQUMsQ0FBQztJQUN2QyxJQUFJLFlBQVksQ0FBQztJQUVqQixJQUFJLFNBQVMsRUFBRTtRQUNYLE1BQU0sS0FBSyxHQUFHO1lBQ1YsWUFBTSxFQUFFO1lBQ1IsV0FBSSxDQUFDLFlBQU0sRUFBRSxFQUFFLElBQUksRUFBRSxJQUFJLEVBQUUsS0FBSyxFQUFFLFFBQVEsQ0FBQztZQUMzQyxXQUFJLENBQUMsWUFBTSxFQUFFLEVBQUUsSUFBSSxFQUFFLElBQUksRUFBRSxjQUFjLENBQUM7U0FDN0MsQ0FBQztRQUVGLGtDQUFrQztRQUNsQyxJQUFJLE9BQU8sQ0FBQyxRQUFRLEtBQUssUUFBUSxFQUFFO1lBQy9CLEtBQUssQ0FBQyxJQUFJLENBQUMsV0FBSSxDQUFDLFlBQU0sRUFBRSxFQUFFLElBQUksRUFBRSxJQUFJLEVBQUUsS0FBSyxFQUFFLE9BQU8sRUFBRSxTQUFTLENBQUMsQ0FBQyxDQUFDO1NBQ3JFO1FBRUQsSUFBSTtZQUNBLE1BQU0sUUFBUSxHQUFHLEtBQUssQ0FBQyxLQUFLLENBQUMsZUFBVSxDQUFDLENBQUM7WUFDekMsWUFBWSxHQUFHLENBQUMsUUFBUSxDQUFDO1NBQzVCO1FBQUMsT0FBTyxHQUFHLEVBQUU7WUFDVixZQUFZLEdBQUcsSUFBSSxDQUFDO1NBQ3ZCO0tBQ0o7U0FBTTtRQUNILFlBQVksR0FBRyxJQUFJLENBQUM7S0FDdkI7SUFFRCxJQUFJLFlBQVksRUFBRTtRQUNkLE9BQU8sSUFBSSxPQUFPLENBQUMsQ0FBQyxPQUFPLEVBQUUsT0FBTyxFQUFFLEVBQUU7WUFDcEMsYUFBTyxDQUFDLE9BQU8sRUFBRSxDQUFDLFFBQWdCLEVBQUUsRUFBRTtnQkFDbEMsZ0JBQWdCLENBQUMsVUFBVSxFQUFFLG1CQUFtQixHQUFHLENBQUMsUUFBUSxHQUFHLEtBQUssQ0FBQyxDQUFDLE9BQU8sQ0FBQyxDQUFDLENBQUMsR0FBRyxHQUFHLENBQUMsQ0FBQztZQUM1RixDQUFDLENBQUMsQ0FBQztRQUNQLENBQUMsQ0FBQyxDQUFDO0tBQ047QUFDTCxDQUFDO0FBakNELGdEQWlDQztBQUVELFNBQWdCLGdCQUFnQjtJQUM1QixPQUFPLFlBQU0sRUFBRSxDQUFDO0FBQ3BCLENBQUM7QUFGRCw0Q0FFQztBQUVELFNBQWdCLGFBQWEsQ0FBQyxPQUFzQjtJQUNoRCxhQUFhO0lBQ2IsT0FBTyxPQUFPLENBQUMsSUFBSSxDQUFDLGNBQVEsRUFBRSxLQUFLLFNBQVMsQ0FBQyxDQUFDLENBQUMsR0FBRyxDQUFDLENBQUMsQ0FBQyxHQUFHLENBQUMsQ0FBQztBQUM5RCxDQUFDO0FBSEQsc0NBR0MifQ==