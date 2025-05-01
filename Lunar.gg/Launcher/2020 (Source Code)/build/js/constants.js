"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.OFFLINE_FILES_DIRECTORY = exports.OFFLINE_JRE_DIRECTORY = exports.SETTINGS_DIRECTORY = exports.DEFAULT_MINECRAFT_DIRECTORY = exports.AAL_DIRECTORY = exports.USER_AGENT = exports.VERSION = exports.HWID = exports.DISCORD_URL = exports.TWITTER_URL = exports.TELEGRAM_URL = exports.TOS_URL = exports.FAQ_URL = exports.WEBSITE_URL = exports.SUPPORT_URL = exports.STORE_URL = exports.LAUNCHER_API_ROOT = void 0;
const node_machine_id_1 = require("node-machine-id");
const os_1 = require("os");
const path_1 = require("path");
const electron_1 = require("electron");
exports.LAUNCHER_API_ROOT = 'https://api.lunarclient.com/launcher/';
exports.STORE_URL = 'https://store.lunarclient.com/launcher';
exports.SUPPORT_URL = 'https://discord.gg/URRzTcw';
exports.WEBSITE_URL = 'https://www.lunarclient.com/';
exports.FAQ_URL = 'https://www.lunarclient.com/faq/';
exports.TOS_URL = 'https://www.lunarclient.com/terms/';
exports.TELEGRAM_URL = 'https://t.me/LunarClient';
exports.TWITTER_URL = 'https://twitter.com/LunarClient';
exports.DISCORD_URL = 'https://discord.com/invite/vZz5vC8';
exports.HWID = node_machine_id_1.machineIdSync();
exports.VERSION = electron_1.remote.app.getVersion();
exports.USER_AGENT = 'Lunar Client Launcher v' + exports.VERSION;
exports.AAL_DIRECTORY = 'C:\\AlphaAntiLeak';
exports.DEFAULT_MINECRAFT_DIRECTORY = defaultMinecraftDirectory();
exports.SETTINGS_DIRECTORY = path_1.join(os_1.homedir(), '.lunarclient', 'settings');
exports.OFFLINE_JRE_DIRECTORY = path_1.join(os_1.homedir(), '.lunarclient', 'offline', 'jre');
exports.OFFLINE_FILES_DIRECTORY = path_1.join(os_1.homedir(), '.lunarclient', 'offline', 'files');
function defaultMinecraftDirectory() {
    switch (os_1.type()) {
        case 'Darwin':
            return path_1.join(os_1.homedir(), '/Library/Application Support/minecraft');
        case 'win32':
        case 'Windows_NT':
            return path_1.join(process.env.APPDATA, '.minecraft');
        default:
            return path_1.join(os_1.homedir(), '.minecraft');
    }
}
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiY29uc3RhbnRzLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vc3JjL2pzL2NvbnN0YW50cy50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFBQSxxREFBZ0Q7QUFDaEQsMkJBQW1DO0FBQ25DLCtCQUE0QjtBQUM1Qix1Q0FBa0M7QUFFckIsUUFBQSxpQkFBaUIsR0FBRyx1Q0FBdUMsQ0FBQztBQUM1RCxRQUFBLFNBQVMsR0FBRyx3Q0FBd0MsQ0FBQztBQUNyRCxRQUFBLFdBQVcsR0FBRyw0QkFBNEIsQ0FBQztBQUMzQyxRQUFBLFdBQVcsR0FBRyw4QkFBOEIsQ0FBQztBQUM3QyxRQUFBLE9BQU8sR0FBRyxrQ0FBa0MsQ0FBQztBQUM3QyxRQUFBLE9BQU8sR0FBRyxvQ0FBb0MsQ0FBQztBQUMvQyxRQUFBLFlBQVksR0FBRywwQkFBMEIsQ0FBQztBQUMxQyxRQUFBLFdBQVcsR0FBRyxpQ0FBaUMsQ0FBQztBQUNoRCxRQUFBLFdBQVcsR0FBRyxvQ0FBb0MsQ0FBQztBQUNuRCxRQUFBLElBQUksR0FBRywrQkFBYSxFQUFFLENBQUM7QUFDdkIsUUFBQSxPQUFPLEdBQUcsaUJBQU0sQ0FBQyxHQUFHLENBQUMsVUFBVSxFQUFFLENBQUM7QUFDbEMsUUFBQSxVQUFVLEdBQUcseUJBQXlCLEdBQUcsZUFBTyxDQUFDO0FBRWpELFFBQUEsYUFBYSxHQUFHLG1CQUFtQixDQUFDO0FBQ3BDLFFBQUEsMkJBQTJCLEdBQUcseUJBQXlCLEVBQUUsQ0FBQztBQUMxRCxRQUFBLGtCQUFrQixHQUFHLFdBQUksQ0FBQyxZQUFPLEVBQUUsRUFBRSxjQUFjLEVBQUUsVUFBVSxDQUFDLENBQUM7QUFDakUsUUFBQSxxQkFBcUIsR0FBRyxXQUFJLENBQUMsWUFBTyxFQUFFLEVBQUUsY0FBYyxFQUFFLFNBQVMsRUFBRSxLQUFLLENBQUMsQ0FBQztBQUMxRSxRQUFBLHVCQUF1QixHQUFHLFdBQUksQ0FBQyxZQUFPLEVBQUUsRUFBRSxjQUFjLEVBQUUsU0FBUyxFQUFFLE9BQU8sQ0FBQyxDQUFDO0FBRTNGLFNBQVMseUJBQXlCO0lBQzlCLFFBQVEsU0FBSSxFQUFFLEVBQUU7UUFDWixLQUFLLFFBQVE7WUFDVCxPQUFPLFdBQUksQ0FBQyxZQUFPLEVBQUUsRUFBRSx3Q0FBd0MsQ0FBQyxDQUFDO1FBQ3JFLEtBQUssT0FBTyxDQUFDO1FBQ2IsS0FBSyxZQUFZO1lBQ2IsT0FBTyxXQUFJLENBQUMsT0FBTyxDQUFDLEdBQUcsQ0FBQyxPQUFPLEVBQUUsWUFBWSxDQUFDLENBQUM7UUFDbkQ7WUFDSSxPQUFPLFdBQUksQ0FBQyxZQUFPLEVBQUUsRUFBRSxZQUFZLENBQUMsQ0FBQztLQUM1QztBQUNMLENBQUMifQ==