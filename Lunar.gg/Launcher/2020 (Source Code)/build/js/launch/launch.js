"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.launchClient = void 0;
const assets_1 = require("./assets");
const apiCalls_1 = require("../apiCalls");
const offline_1 = require("./types/offline");
const alphaantileak_1 = require("./types/alphaantileak");
const shadowguard_1 = require("./types/shadowguard");
const settings_1 = require("../settings");
const log = require('electron-log').scope('launch');
async function launchClient(autoJoin, launchType, version, branch, callbacks) {
    const [width, height] = settings_1.getResolution();
    const launchDirectory = settings_1.getLaunchDirectory();
    const allocatedMemoryMb = settings_1.getAllocatedMemory();
    const programArgs = [
        '--newLauncher',
        '--version', 'Lunar Client',
        '--accessToken', '0',
        '--assetIndex', version.assets.id,
        '--userProperties', '{}',
        '--gameDir', launchDirectory,
        '--width', width.toString(),
        '--height', height.toString()
    ];
    if (autoJoin != null) {
        programArgs.push('--server');
        programArgs.push(autoJoin.ip);
    }
    callbacks.progress('Updating', 'Updating MC Assets....');
    // don't fail to launch if we can't download assets
    try {
        await assets_1.downloadAssets(version.assets);
    }
    catch (err) {
        log.warn('Could not download assets: ' + err);
    }
    callbacks.progress('Authenticating', 'Contacting LC servers...');
    log.info('Launching client: ' + launchType);
    let statusInitTime = -1;
    let statusStartedTime = -1;
    let logs = [];
    const started = new Date().getTime();
    const wrappedCallbacks = {
        progress: callbacks.progress,
        success: callbacks.success,
        log: message => {
            // convert Buffer to string
            message = message.toString();
            if (statusInitTime === -1 && message.includes('LUNARCLIENT_STATUS_INIT')) {
                statusInitTime = new Date().getTime();
                callbacks.success();
            }
            else if (statusStartedTime === -1 && message.includes('LUNARCLIENT_STATUS_STARTED')) {
                statusStartedTime = new Date().getTime();
                logs = []; // just to free some memory
                apiCalls_1.reportLaunchSuccess({
                    type: launchType,
                    timeToInit: statusInitTime - started,
                    timeToStarted: statusStartedTime - started,
                    os: process.platform,
                    version: version.id
                });
            }
            // only store log messages if we haven't started
            if (statusStartedTime === -1) {
                logs.push(message);
            }
            if (message) {
                callbacks.log(message);
            }
        },
        exit: () => {
            if (statusStartedTime === -1) {
                apiCalls_1.reportLaunchFail({
                    type: launchType,
                    timeToInit: statusInitTime - started,
                    timeToStarted: statusStartedTime - started,
                    os: process.platform,
                    version: version.id,
                    logs: logs
                }).then(() => {
                    log.info('Launch fail reported.');
                });
            }
            else {
                callbacks.exit();
            }
        }
    };
    switch (launchType) {
        case apiCalls_1.LaunchType.ALPHA_ANTI_LEAK:
            await alphaantileak_1.launch(programArgs, allocatedMemoryMb, version, branch, wrappedCallbacks);
            return;
        case apiCalls_1.LaunchType.SHADOW_GUARD:
            await shadowguard_1.launch(programArgs, allocatedMemoryMb, version, branch, wrappedCallbacks);
            return;
        case apiCalls_1.LaunchType.OFFLINE:
            await offline_1.launch(programArgs, allocatedMemoryMb, version, branch, wrappedCallbacks);
            return;
    }
}
exports.launchClient = launchClient;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoibGF1bmNoLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL2pzL2xhdW5jaC9sYXVuY2gudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEscUNBQTBDO0FBQzFDLDBDQUFpRztBQUNqRyw2Q0FBMEQ7QUFDMUQseURBQXNFO0FBQ3RFLHFEQUFrRTtBQUNsRSwwQ0FBb0Y7QUFDcEYsTUFBTSxHQUFHLEdBQUcsT0FBTyxDQUFDLGNBQWMsQ0FBQyxDQUFDLEtBQUssQ0FBQyxRQUFRLENBQUMsQ0FBQztBQWdCN0MsS0FBSyxVQUFVLFlBQVksQ0FBQyxRQUF1QixFQUFFLFVBQXNCLEVBQUUsT0FBZ0IsRUFBRSxNQUFjLEVBQUUsU0FBMEI7SUFDOUksTUFBTSxDQUFDLEtBQUssRUFBRSxNQUFNLENBQUMsR0FBRyx3QkFBYSxFQUFFLENBQUM7SUFDeEMsTUFBTSxlQUFlLEdBQUcsNkJBQWtCLEVBQUUsQ0FBQztJQUM3QyxNQUFNLGlCQUFpQixHQUFHLDZCQUFrQixFQUFFLENBQUM7SUFFL0MsTUFBTSxXQUFXLEdBQUc7UUFDbEIsZUFBZTtRQUNmLFdBQVcsRUFBRSxjQUFjO1FBQzNCLGVBQWUsRUFBRSxHQUFHO1FBQ3BCLGNBQWMsRUFBRSxPQUFPLENBQUMsTUFBTSxDQUFDLEVBQUU7UUFDakMsa0JBQWtCLEVBQUUsSUFBSTtRQUN4QixXQUFXLEVBQUUsZUFBZTtRQUM1QixTQUFTLEVBQUUsS0FBSyxDQUFDLFFBQVEsRUFBRTtRQUMzQixVQUFVLEVBQUUsTUFBTSxDQUFDLFFBQVEsRUFBRTtLQUM5QixDQUFDO0lBRUYsSUFBSSxRQUFRLElBQUksSUFBSSxFQUFFO1FBQ3BCLFdBQVcsQ0FBQyxJQUFJLENBQUMsVUFBVSxDQUFDLENBQUM7UUFDN0IsV0FBVyxDQUFDLElBQUksQ0FBQyxRQUFRLENBQUMsRUFBRSxDQUFDLENBQUM7S0FDL0I7SUFFRCxTQUFTLENBQUMsUUFBUSxDQUFDLFVBQVUsRUFBRSx3QkFBd0IsQ0FBQyxDQUFD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