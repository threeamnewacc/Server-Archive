"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.launch = void 0;
async function launch(_programArgs, _allocatedMemoryMb, _version, _branch, _callbacks) {
    // let offlinePath = join(OFFLINE_FILES_DIRECTORY, version.id);
    // await fs.mkdir(offlinePath, { recursive: true });
    //
    // const hashes = await collectFileCacheHashes(offlinePath);
    // const response = await makeLaunchRequest(version, branch, LaunchType.SHADOW_GUARD, { hashes: hashes });
    // await updateFileCache(response.updates, offlinePath, callbacks.progress);
    //
    // callbacks.progress('Launching', 'Extracting natives...');
    // await extractNatives(offlinePath);
    //
    // await ensureJREInstalled(callbacks.progress);
    //
    // callbacks.progress('Launching', 'Starting JVM');
    //
    // const needsUpdate = await Launcher.needsUpdate();
    // console.log("Needs Update:", needsUpdate);
    //
    // if (needsUpdate) {
    //     await Launcher.update(console.log)
    // }
    //
    // const command = shellEscape([
    //     getJREExecutable(),
    //     `-Xms${allocatedMemoryMb}m`,
    //     `-Xmx${allocatedMemoryMb}m`,
    //     '-Djava.library.path=natives',
    //     '-cp',
    //     joinClasspath(['libraries', 'assets_lunar', 'assets_minecraft', 'client']),
    //     'Start'
    // ].concat(programArgs));
    //
    // await Launcher.launch("773c295a-0825-4a08-9088-fa2e0c34f069", command, {
    //     error: message => {
    //         throw {
    //             short: 'Failed to start anticheat',
    //             description: message
    //         }
    //     },
    //     progress: (phase, progress) => {
    //         callbacks.progress('Launching', phase + " " + progress);
    //     },
    //     output: (_error, content) => callbacks.log(content)
    // }, offlinePath).then(code => {
    //     console.log(code);
    //     callbacks.exit()
    // }).catch(console.error);
}
exports.launch = launch;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic2hhZG93Z3VhcmQuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9zcmMvanMvbGF1bmNoL3R5cGVzL3NoYWRvd2d1YXJkLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7OztBQUdPLEtBQUssVUFBVSxNQUFNLENBQUMsWUFBMkIsRUFBRSxrQkFBMEIsRUFBRSxRQUFpQixFQUFFLE9BQWUsRUFBRSxVQUEyQjtJQUNqSiwrREFBK0Q7SUFDL0Qsb0RBQW9EO0lBQ3BELEVBQUU7SUFDRiw0REFBNEQ7SUFDNUQsMEdBQTBHO0lBQzFHLDRFQUE0RTtJQUM1RSxFQUFFO0lBQ0YsNERBQTREO0lBQzVELHFDQUFxQztJQUNyQyxFQUFFO0lBQ0YsZ0RBQWdEO0lBQ2hELEVBQUU7SUFDRixtREFBbUQ7SUFDbkQsRUFBRTtJQUNGLG9EQUFvRDtJQUNwRCw2Q0FBNkM7SUFDN0MsRUFBRTtJQUNGLHFCQUFxQjtJQUNyQix5Q0FBeUM7SUFDekMsSUFBSTtJQUNKLEVBQUU7SUFDRixnQ0FBZ0M7SUFDaEMsMEJBQTBCO0lBQzFCLG1DQUFtQztJQUNuQyxtQ0FBbUM7SUFDbkMscUNBQXFDO0lBQ3JDLGFBQWE7SUFDYixrRkFBa0Y7SUFDbEYsY0FBYztJQUNkLDBCQUEwQjtJQUMxQixFQUFFO0lBQ0YsMkVBQTJFO0lBQzNFLDBCQUEwQjtJQUMxQixrQkFBa0I7SUFDbEIsa0RBQWtEO0lBQ2xELG1DQUFtQztJQUNuQyxZQUFZO0lBQ1osU0FBUztJQUNULHVDQUF1QztJQUN2QyxtRUFBbUU7SUFDbkUsU0FBUztJQUNULDBEQUEwRDtJQUMxRCxpQ0FBaUM7SUFDakMseUJBQXlCO0lBQ3pCLHVCQUF1QjtJQUN2QiwyQkFBMkI7QUFDL0IsQ0FBQztBQS9DRCx3QkErQ0MifQ==