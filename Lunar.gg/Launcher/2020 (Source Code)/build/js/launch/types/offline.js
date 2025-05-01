"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.launch = void 0;
const apiCalls_1 = require("../../apiCalls");
const fs_1 = require("fs");
const constants_1 = require("../../constants");
const jre_1 = require("../jre");
const child_process_1 = require("child_process");
const path_1 = require("path");
const releaseFiles_1 = require("../releaseFiles");
async function launch(programArgs, allocatedMemoryMb, version, branch, callbacks) {
    let offlinePath = path_1.join(constants_1.OFFLINE_FILES_DIRECTORY, version.id);
    await fs_1.promises.mkdir(offlinePath, { recursive: true });
    const hashes = await releaseFiles_1.collectFileCacheHashes(offlinePath);
    const response = await apiCalls_1.makeLaunchRequest(version, branch, apiCalls_1.LaunchType.OFFLINE, { hashes: hashes });
    await releaseFiles_1.updateFileCache(response.updates, offlinePath, callbacks.progress);
    callbacks.progress('Launching', 'Extracting natives...');
    await releaseFiles_1.extractNatives(offlinePath);
    await jre_1.ensureJREInstalled(callbacks.progress);
    callbacks.progress('Launching', 'Starting JVM');
    let process = child_process_1.spawn(jre_1.getJREExecutable(), [
        `-Xms${allocatedMemoryMb}m`,
        `-Xmx${allocatedMemoryMb}m`,
        '-Djava.library.path=natives',
        '-XX:+DisableAttachMechanism',
        '-cp',
        jre_1.joinClasspath(['libraries', 'assets_lunar', 'assets_minecraft', 'client']),
        'Start'
    ].concat(programArgs), {
        cwd: offlinePath,
        detached: true
    });
    process.stdout.on('data', callbacks.log);
    process.stderr.on('data', callbacks.log);
    process.on('exit', _code => callbacks.exit());
}
exports.launch = launch;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoib2ZmbGluZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3NyYy9qcy9sYXVuY2gvdHlwZXMvb2ZmbGluZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFBQSw2Q0FBd0U7QUFFeEUsMkJBQW9DO0FBQ3BDLCtDQUEwRDtBQUMxRCxnQ0FBNkU7QUFDN0UsaURBQXNDO0FBQ3RDLCtCQUE0QjtBQUM1QixrREFBMEY7QUFFbkYsS0FBSyxVQUFVLE1BQU0sQ0FBQyxXQUEwQixFQUFFLGlCQUF5QixFQUFFLE9BQWdCLEVBQUUsTUFBYyxFQUFFLFNBQTBCO0lBQzVJLElBQUksV0FBVyxHQUFHLFdBQUksQ0FBQyxtQ0FBdUIsRUFBRSxPQUFPLENBQUMsRUFBRSxDQUFDLENBQUM7SUFDNUQsTUFBTSxhQUFFLENBQUMsS0FBSyxDQUFDLFdBQVcsRUFBRSxFQUFFLFNBQVMsRUFBRSxJQUFJLEVBQUUsQ0FBQyxDQUFDO0lBRWpELE1BQU0sTUFBTSxHQUFHLE1BQU0scUNBQXNCLENBQUMsV0FBVyxDQUFDLENBQUM7SUFDekQsTUFBTSxRQUFRLEdBQUcsTUFBTSw0QkFBaUIsQ0FBQyxPQUFPLEVBQUUsTUFBTSxFQUFFLHFCQUFVLENBQUMsT0FBTyxFQUFFLEVBQUUsTUFBTSxFQUFFLE1BQU0sRUFBRSxDQUFDLENBQUM7SUFDbEcsTUFBTSw4QkFBZSxDQUFDLFFBQVEsQ0FBQyxPQUFPLEVBQUUsV0FBVyxFQUFFLFNBQVMsQ0FBQyxRQUFRLENBQUMsQ0FBQztJQUV6RSxTQUFTLENBQUMsUUFBUSxDQUFDLFdBQVcsRUFBRSx1QkFBdUIsQ0FBQyxDQUFDO0lBQ3pELE1BQU0sNkJBQWMsQ0FBQyxXQUFXLENBQUMsQ0FBQztJQUVsQyxNQUFNLHdCQUFrQixDQUFDLFNBQVMsQ0FBQyxRQUFRLENBQUMsQ0FBQztJQUU3QyxTQUFTLENBQUMsUUFBUSxDQUFDLFdBQVcsRUFBRSxjQUFjLENBQUMsQ0FBQztJQUVoRCxJQUFJLE9BQU8sR0FBRyxxQkFBSyxDQUNmLHNCQUFnQixFQUFFLEVBQ2xCO1FBQ0ksT0FBTyxpQkFBaUIsR0FBRztRQUMzQixPQUFPLGlCQUFpQixHQUFHO1FBQzNCLDZCQUE2QjtRQUM3Qiw2QkFBNkI7UUFDN0IsS0FBSztRQUNMLG1CQUFhLENBQUMsQ0FBQyxXQUFXLEVBQUUsY0FBYyxFQUFFLGtCQUFrQixFQUFFLFFBQVEsQ0FBQyxDQUFDO1FBQzFFLE9BQU87S0FDVixDQUFDLE1BQU0sQ0FBQyxXQUFXLENBQUMsRUFDckI7UUFDSSxHQUFHLEVBQUUsV0FBVztRQUNoQixRQUFRLEVBQUUsSUFBSTtLQUNqQixDQUNKLENBQUM7SUFFRixPQUFPLENBQUMsTUFBTSxDQUFDLEVBQUUsQ0FBQyxNQUFNLEVBQUUsU0FBUyxDQUFDLEdBQUcsQ0FBQyxDQUFDO0lBQ3pDLE9BQU8sQ0FBQyxNQUFNLENBQUMsRUFBRSxDQUFDLE1BQU0sRUFBRSxTQUFTLENBQUMsR0FBRyxDQUFDLENBQUM7SUFDekMsT0FBTyxDQUFDLEVBQUUsQ0FBQyxNQUFNLEVBQUUsS0FBSyxDQUFDLEVBQUUsQ0FBQyxTQUFTLENBQUMsSUFBSSxFQUFFLENBQUMsQ0FBQztBQUNsRCxDQUFDO0FBbkNELHdCQW1DQyJ9