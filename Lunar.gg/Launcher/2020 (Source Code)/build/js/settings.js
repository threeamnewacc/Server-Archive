"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.getResolution = exports.setResolution = exports.getLaunchDirectory = exports.setLaunchDirectory = exports.isAnticheatEnabled = exports.setAnticheatEnabled = exports.getSelectedVersion = exports.setSelectedVersion = exports.getAllocatedMemory = exports.setAllocatedMemory = exports.getExperimentalBranch = exports.setExperimentalBranch = exports.getExperimentalEnabled = exports.setExperimentalEnabled = exports.getAfterLaunchAction = exports.setAfterLaunchAction = void 0;
const constants_1 = require("./constants");
const Store = require("electron-store");
const aal_1 = require("./lib/aal");
const store = new Store({
    cwd: constants_1.SETTINGS_DIRECTORY,
    name: 'launcher'
});
function setAfterLaunchAction(action) {
    store.set('afterLaunchAction', action);
}
exports.setAfterLaunchAction = setAfterLaunchAction;
function getAfterLaunchAction() {
    return store.get('afterLaunchAction', 'HIDE');
}
exports.getAfterLaunchAction = getAfterLaunchAction;
function setExperimentalEnabled(enabled) {
    store.set('experimental.enabled', enabled);
}
exports.setExperimentalEnabled = setExperimentalEnabled;
function getExperimentalEnabled() {
    return store.get('experimental.enabled', false);
}
exports.getExperimentalEnabled = getExperimentalEnabled;
function setExperimentalBranch(branch) {
    store.set('experimental.branch', branch);
}
exports.setExperimentalBranch = setExperimentalBranch;
function getExperimentalBranch() {
    return store.get('experimental.branch', 'development');
}
exports.getExperimentalBranch = getExperimentalBranch;
function setAllocatedMemory(memory) {
    store.set('allocatedMemory', memory);
}
exports.setAllocatedMemory = setAllocatedMemory;
function getAllocatedMemory() {
    return store.get('allocatedMemory', 3072);
}
exports.getAllocatedMemory = getAllocatedMemory;
function setSelectedVersion(version) {
    store.set('selectedVersion', version);
}
exports.setSelectedVersion = setSelectedVersion;
function getSelectedVersion() {
    return store.get('selectedVersion', '1.8.9');
}
exports.getSelectedVersion = getSelectedVersion;
function setAnticheatEnabled(version, enabled) {
    store.set('anticheatEnabled.' + version, enabled);
}
exports.setAnticheatEnabled = setAnticheatEnabled;
function isAnticheatEnabled(version) {
    return store.get('anticheatEnabled.' + version, aal_1.isCompatibleWithSystem());
}
exports.isAnticheatEnabled = isAnticheatEnabled;
function setLaunchDirectory(directory) {
    store.set('launchDirectory', directory);
}
exports.setLaunchDirectory = setLaunchDirectory;
function getLaunchDirectory() {
    return store.get('launchDirectory', constants_1.DEFAULT_MINECRAFT_DIRECTORY);
}
exports.getLaunchDirectory = getLaunchDirectory;
function setResolution(width, height) {
    store.set('resolution.width', width);
    store.set('resolution.height', height);
}
exports.setResolution = setResolution;
function getResolution() {
    return [store.get('resolution.width', 854), store.get('resolution.height', 480)];
}
exports.getResolution = getResolution;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic2V0dGluZ3MuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvanMvc2V0dGluZ3MudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsMkNBQThFO0FBQzlFLHdDQUF5QztBQUN6QyxtQ0FBbUQ7QUFFbkQsTUFBTSxLQUFLLEdBQUcsSUFBSSxLQUFLLENBQUM7SUFDcEIsR0FBRyxFQUFFLDhCQUFrQjtJQUN2QixJQUFJLEVBQUUsVUFBVTtDQUNuQixDQUFDLENBQUM7QUFFSCxTQUFnQixvQkFBb0IsQ0FBQyxNQUFzQztJQUN2RSxLQUFLLENBQUMsR0FBRyxDQUFDLG1CQUFtQixFQUFFLE1BQU0sQ0FBQyxDQUFDO0FBQzNDLENBQUM7QUFGRCxvREFFQztBQUVELFNBQWdCLG9CQUFvQjtJQUNoQyxPQUFPLEtBQUssQ0FBQyxHQUFHLENBQUMsbUJBQW1CLEVBQUUsTUFBTSxDQUFDLENBQUM7QUFDbEQsQ0FBQztBQUZELG9EQUVDO0FBRUQsU0FBZ0Isc0JBQXNCLENBQUMsT0FBZ0I7SUFDbkQsS0FBSyxDQUFDLEdBQUcsQ0FBQyxzQkFBc0IsRUFBRSxPQUFPLENBQUMsQ0FBQztBQUMvQyxDQUFDO0FBRkQsd0RBRUM7QUFFRCxTQUFnQixzQkFBc0I7SUFDbEMsT0FBTyxLQUFLLENBQUMsR0FBRyxDQUFDLHNCQUFzQixFQUFFLEtBQUssQ0FBQyxDQUFBO0FBQ25ELENBQUM7QUFGRCx3REFFQztBQUVELFNBQWdCLHFCQUFxQixDQUFDLE1BQWM7SUFDaEQsS0FBSyxDQUFDLEdBQUcsQ0FBQyxxQkFBcUIsRUFBRSxNQUFNLENBQUMsQ0FBQztBQUM3QyxDQUFDO0FBRkQsc0RBRUM7QUFFRCxTQUFnQixxQkFBcUI7SUFDakMsT0FBTyxLQUFLLENBQUMsR0FBRyxDQUFDLHFCQUFxQixFQUFFLGFBQWEsQ0FBQyxDQUFBO0FBQzFELENBQUM7QUFGRCxzREFFQztBQUVELFNBQWdCLGtCQUFrQixDQUFDLE1BQWM7SUFDN0MsS0FBSyxDQUFDLEdBQUcsQ0FBQyxpQkFBaUIsRUFBRSxNQUFNLENBQUMsQ0FBQztBQUN6QyxDQUFDO0FBRkQsZ0RBRUM7QUFFRCxTQUFnQixrQkFBa0I7SUFDOUIsT0FBTyxLQUFLLENBQUMsR0FBRyxDQUFDLGlCQUFpQixFQUFFLElBQUksQ0FBQyxDQUFDO0FBQzlDLENBQUM7QUFGRCxnREFFQztBQUVELFNBQWdCLGtCQUFrQixDQUFDLE9BQWU7SUFDOUMsS0FBSyxDQUFDLEdBQUcsQ0FBQyxpQkFBaUIsRUFBRSxPQUFPLENBQUMsQ0FBQztBQUMxQyxDQUFDO0FBRkQsZ0RBRUM7QUFFRCxTQUFnQixrQkFBa0I7SUFDOUIsT0FBTyxLQUFLLENBQUMsR0FBRyxDQUFDLGlCQUFpQixFQUFFLE9BQU8sQ0FBQyxDQUFDO0FBQ2pELENBQUM7QUFGRCxnREFFQztBQUVELFNBQWdCLG1CQUFtQixDQUFDLE9BQWUsRUFBRSxPQUFnQjtJQUNqRSxLQUFLLENBQUMsR0FBRyxDQUFDLG1CQUFtQixHQUFHLE9BQU8sRUFBRSxPQUFPLENBQUMsQ0FBQztBQUN0RCxDQUFDO0FBRkQsa0RBRUM7QUFFRCxTQUFnQixrQkFBa0IsQ0FBQyxPQUFlO0lBQzlDLE9BQU8sS0FBSyxDQUFDLEdBQUcsQ0FBQyxtQkFBbUIsR0FBRyxPQUFPLEVBQUUsNEJBQXNCLEVBQUUsQ0FBQyxDQUFDO0FBQzlFLENBQUM7QUFGRCxnREFFQztBQUVELFNBQWdCLGtCQUFrQixDQUFDLFNBQWlCO0lBQ2hELEtBQUssQ0FBQyxHQUFHLENBQUMsaUJBQWlCLEVBQUUsU0FBUyxDQUFDLENBQUM7QUFDNUMsQ0FBQztBQUZELGdEQUVDO0FBRUQsU0FBZ0Isa0JBQWtCO0lBQzlCLE9BQU8sS0FBSyxDQUFDLEdBQUcsQ0FBQyxpQkFBaUIsRUFBRSx1Q0FBMkIsQ0FBQyxDQUFDO0FBQ3JFLENBQUM7QUFGRCxnREFFQztBQUVELFNBQWdCLGFBQWEsQ0FBQyxLQUFhLEVBQUUsTUFBYztJQUN2RCxLQUFLLENBQUMsR0FBRyxDQUFDLGtCQUFrQixFQUFFLEtBQUssQ0FBQyxDQUFDO0lBQ3JDLEtBQUssQ0FBQyxHQUFHLENBQUMsbUJBQW1CLEVBQUUsTUFBTSxDQUFDLENBQUM7QUFDM0MsQ0FBQztBQUhELHNDQUdDO0FBRUQsU0FBZ0IsYUFBYTtJQUN6QixPQUFPLENBQUMsS0FBSyxDQUFDLEdBQUcsQ0FBQyxrQkFBa0IsRUFBRSxHQUFHLENBQUMsRUFBRSxLQUFLLENBQUMsR0FBRyxDQUFDLG1CQUFtQixFQUFFLEdBQUcsQ0FBQyxDQUFDLENBQUE7QUFDcEYsQ0FBQztBQUZELHNDQUVDIn0=