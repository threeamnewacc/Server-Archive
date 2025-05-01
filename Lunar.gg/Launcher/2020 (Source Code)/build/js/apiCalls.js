"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.makeLaunchRequest = exports.requestMetadata = exports.reportLaunchFail = exports.reportLaunchSuccess = exports.LaunchType = void 0;
const log = require("electron-log");
const constants_1 = require("./constants");
var LaunchType;
(function (LaunchType) {
    LaunchType["OFFLINE"] = "OFFLINE";
    LaunchType["ALPHA_ANTI_LEAK"] = "ALPHA_ANTI_LEAK";
    LaunchType["SHADOW_GUARD"] = "SHADOW_GUARD";
})(LaunchType = exports.LaunchType || (exports.LaunchType = {}));
async function reportLaunchSuccess(data) {
    await reportLaunchStatus(true, data);
}
exports.reportLaunchSuccess = reportLaunchSuccess;
async function reportLaunchFail(data) {
    await reportLaunchStatus(false, data);
}
exports.reportLaunchFail = reportLaunchFail;
async function requestMetadata() {
    log.info('Making metadata request...');
    const response = await fetch(constants_1.LAUNCHER_API_ROOT + 'metadata', {
        method: 'POST',
        headers: {
            'User-Agent': constants_1.USER_AGENT
        },
        body: JSON.stringify({
            new_launcher: true,
            hwid: constants_1.HWID,
            launcher_version: constants_1.VERSION
        })
    });
    log.info('Received metadata response, status is ' + response.status);
    return await response.json();
}
exports.requestMetadata = requestMetadata;
async function makeLaunchRequest(version, branch, launchType, launchTypeData) {
    log.info('Making launch request...');
    console.log(JSON.stringify({
        new_launcher: true,
        hwid: constants_1.HWID,
        launcher_version: constants_1.VERSION,
        version: version.id,
        branch: branch,
        launch_type: launchType,
        launch_type_data: launchTypeData
    }));
    
    const response = await fetch(constants_1.LAUNCHER_API_ROOT + 'launch', {
        method: 'POST',
        headers: {
            'User-Agent': constants_1.USER_AGENT
        },
        body: JSON.stringify({
            new_launcher: true,
            hwid: constants_1.HWID,
            launcher_version: constants_1.VERSION,
            version: version.id,
            branch: branch,
            launch_type: launchType,
            launch_type_data: launchTypeData
        })
    });
    log.info('Received launch response, status is ' + response.status);
    const body = await response.json();
    log.info('Body is ' + JSON.stringify(body));
    if (!body.success) {
        throw {
            short: body.error.short,
            description: body.error.message
        };
    }
    return body.launch_type_data;
}
exports.makeLaunchRequest = makeLaunchRequest;
// private functions
async function reportLaunchStatus(success, data) {
    log.info('Making report request... ' + JSON.stringify(data));
    await fetch(constants_1.LAUNCHER_API_ROOT + 'reportLaunchStatus', {
        method: 'POST',
        headers: {
            'User-Agent': constants_1.USER_AGENT
        },
        body: JSON.stringify({
            new_launcher: true,
            hwid: constants_1.HWID,
            launcher_version: constants_1.VERSION,
            success: success,
            data: data
        })
    });
}
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiYXBpQ2FsbHMuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvanMvYXBpQ2FsbHMudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsb0NBQXFDO0FBQ3JDLDJDQUEyRTtBQXdEM0UsSUFBWSxVQUlYO0FBSkQsV0FBWSxVQUFVO0lBQ2xCLGlDQUFtQixDQUFBO0lBQ25CLGlEQUFtQyxDQUFBO0lBQ25DLDJDQUE2QixDQUFBO0FBQ2pDLENBQUMsRUFKVyxVQUFVLEdBQVYsa0JBQVUsS0FBVixrQkFBVSxRQUlyQjtBQUVNLEtBQUssVUFBVSxtQkFBbUIsQ0FBQyxJQUF1QjtJQUM3RCxNQUFNLGtCQUFrQixDQUFDLElBQUksRUFBRSxJQUFJLENBQUMsQ0FBQztBQUN6QyxDQUFDO0FBRkQsa0RBRUM7QUFFTSxLQUFLLFVBQVUsZ0JBQWdCLENBQUMsSUFBb0I7SUFDdkQsTUFBTSxrQkFBa0IsQ0FBQyxLQUFLLEVBQUUsSUFBSSxDQUFDLENBQUM7QUFDMUMsQ0FBQztBQUZELDRDQUVDO0FBRU0sS0FBSyxVQUFVLGVBQWU7SUFDakMsR0FBRyxDQUFDLElBQUksQ0FBQyw0QkFBNEIsQ0FBQyxDQUFDO0lBRXZDLE1BQU0sUUFBUSxHQUFHLE1BQU0sS0FBSyxDQUFDLDZCQUFpQixHQUFHLFVBQVUsRUFBRTtRQUN6RCxNQUFNLEVBQUUsTUFBTTtRQUNkLE9BQU8sRUFBRTtZQUNMLFlBQVksRUFBRSxzQkFBVTtTQUMzQjtRQUNELElBQUksRUFBRSxJQUFJLENBQUMsU0FBUyxDQUFDO1lBQ2pCLFlBQVksRUFBRSxJQUFJO1lBQ2xCLElBQUksRUFBRSxnQkFBSTtZQUNWLGdCQUFnQixFQUFFLG1CQUFPO1NBQzVCLENBQUM7S0FDTCxDQUFDLENBQUM7SUFFSCxHQUFHLENBQUMsSUFBSSxDQUFDLHdDQUF3QyxHQUFHLFFBQVEsQ0FBQyxNQUFNLENBQUMsQ0FBQztJQUNyRSxPQUFPLE1BQU0sUUFBUSxDQUFDLElBQUksRUFBRSxDQUFDO0FBQ2pDLENBQUM7QUFqQkQsMENBaUJDO0FBRU0sS0FBSyxVQUFVLGlCQUFpQixDQUFDLE9BQWdCLEVBQUUsTUFBYyxFQUFFLFVBQXNCLEVBQUUsY0FBbUI7SUFDakgsR0FBRyxDQUFDLElBQUksQ0FBQywwQkFBMEIsQ0FBQyxDQUFDO0lBRXJDLE1BQU0sUUFBUSxHQUFHLE1BQU0sS0FBSyxDQUFDLDZCQUFpQixHQUFHLFFBQVEsRUFBRTtRQUN2RCxNQUFNLEVBQUUsTUFBTTtRQUNkLE9BQU8sRUFBRTtZQUNMLFlBQVksRUFBRSxzQkFBVTtTQUMzQjtRQUNELElBQUksRUFBRSxJQUFJLENBQUMsU0FBUyxDQUFDO1lBQ2pCLFlBQVksRUFBRSxJQUFJO1lBQ2xCLElBQUksRUFBRSxnQkFBSTtZQUNWLGdCQUFnQixFQUFFLG1CQUFPO1lBQ3pCLE9BQU8sRUFBRSxPQUFPLENBQUMsRUFBRTtZQUNuQixNQUFNLEVBQUUsTUFBTTtZQUNkLFdBQVcsRUFBRSxVQUFVO1lBQ3ZCLGdCQUFnQixFQUFFLGNBQWM7U0FDbkMsQ0FBQztLQUNMLENBQUMsQ0FBQztJQUVILEdBQUcsQ0FBQyxJQUFJLENBQUMsc0NBQXNDLEdBQUcsUUFBUSxDQUFDLE1BQU0sQ0FBQyxDQUFDO0lBQ25FLE1BQU0sSUFBSSxHQUFHLE1BQU0sUUFBUSxDQUFDLElBQUksRUFBRSxDQUFDO0lBQ25DLEdBQUcsQ0FBQyxJQUFJLENBQUMsVUFBVSxHQUFHLElBQUksQ0FBQyxTQUFTLENBQUMsSUFBSSxDQUFDLENBQUMsQ0FBQztJQUU1QyxJQUFJLENBQUMsSUFBSSxDQUFDLE9BQU8sRUFBRTtRQUNmLE1BQU07WUFDRixLQUFLLEVBQUUsSUFBSSxDQUFDLEtBQUssQ0FBQyxLQUFLO1lBQ3ZCLFdBQVcsRUFBRSxJQUFJLENBQUMsS0FBSyxDQUFDLE9BQU87U0FDbEMsQ0FBQztLQUNMO0lBRUQsT0FBTyxJQUFJLENBQUMsZ0JBQWdCLENBQUM7QUFDakMsQ0FBQztBQS9CRCw4Q0ErQkM7QUFFRCxvQkFBb0I7QUFDcEIsS0FBSyxVQUFVLGtCQUFrQixDQUFDLE9BQWdCLEVBQUUsSUFBd0M7SUFDeEYsR0FBRyxDQUFDLElBQUksQ0FBQywyQkFBMkIsR0FBRyxJQUFJLENBQUMsU0FBUyxDQUFDLElBQUksQ0FBQyxDQUFDLENBQUM7SUFFN0QsTUFBTSxLQUFLLENBQUMsNkJBQWlCLEdBQUcsb0JBQW9CLEVBQUU7UUFDbEQsTUFBTSxFQUFFLE1BQU07UUFDZCxPQUFPLEVBQUU7WUFDTCxZQUFZLEVBQUUsc0JBQVU7U0FDM0I7UUFDRCxJQUFJLEVBQUUsSUFBSSxDQUFDLFNBQVMsQ0FBQztZQUNqQixZQUFZLEVBQUUsSUFBSTtZQUNsQixJQUFJLEVBQUUsZ0JBQUk7WUFDVixnQkFBZ0IsRUFBRSxtQkFBTztZQUN6QixPQUFPLEVBQUUsT0FBTztZQUNoQixJQUFJLEVBQUUsSUFBSTtTQUNiLENBQUM7S0FDTCxDQUFDLENBQUM7QUFDUCxDQUFDIn0=