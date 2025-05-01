"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const electron_1 = require("electron");
const path_1 = require("path");
const os_1 = require("os");
const log = require("electron-log");
// change path to be in .lunarclient folder
log.transports.file.file = path_1.join(os_1.homedir(), '.lunarclient', 'logs', 'launcher', log.transports.file.fileName);
let loadingWindow;
let mainWindow;
function createMainWindow() {
    mainWindow = new electron_1.BrowserWindow({
        width: 1300,
        height: 800,
        frame: false,
        show: false,
        transparent: true,
        resizable: false,
        fullscreenable: false,
        titleBarStyle: 'customButtonsOnHover',
        webPreferences: {
            nodeIntegration: true,
            enableRemoteModule: true
        }
    });
    mainWindow.on('close', () => mainWindow.removeAllListeners());
    mainWindow.on('closed', () => { mainWindow = null; });
    mainWindow.loadFile('src/index.html');
    // mainWindow.webContents.openDevTools();
    electron_1.ipcMain.on('ready', () => {
        // null check is needed for if the loading window is closed (either via
        // window control or task bar) before loading is complete
        // close loading window
        if (loadingWindow !== null) {
            loadingWindow.close();
        }
        // show main window
        if (mainWindow !== null) {
            mainWindow.show();
        }
    });
}
function createLoadingWindow() {
    loadingWindow = new electron_1.BrowserWindow({
        width: 300,
        height: 400,
        frame: false,
        show: false,
        resizable: false,
        titleBarStyle: 'customButtonsOnHover',
        backgroundColor: '#1c1a1b',
        webPreferences: {
            enableRemoteModule: false
        }
    });
    loadingWindow.on('close', () => loadingWindow.removeAllListeners());
    loadingWindow.on('closed', () => { loadingWindow = null; });
    loadingWindow.on('ready-to-show', () => loadingWindow.show());
    loadingWindow.webContents.on('devtools-opened', () => loadingWindow.webContents.closeDevTools());
    loadingWindow.loadFile('src/loading.html');
}
if (process.platform === 'darwin') {
    const menu = electron_1.Menu.buildFromTemplate([
        {
            label: 'Lunar Client',
            submenu: [
                { role: 'about', label: 'About Lunar Client' },
                { type: 'separator' },
                { role: 'hide', label: 'Hide Lunar Client' },
                { role: 'unhide' },
                { type: 'separator' },
                { role: 'quit', label: 'Quit Lunar Client' }
            ]
        }
    ]);
    electron_1.Menu.setApplicationMenu(menu);
}
electron_1.app.on('ready', () => {
    require('./autoUpdater');
    createLoadingWindow();
    createMainWindow();
});
electron_1.app.on('window-all-closed', () => {
    electron_1.app.quit();
});
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoibWFpbi5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uL3NyYy9tYWluLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7O0FBQUEsdUNBQTZEO0FBQzdELCtCQUE0QjtBQUM1QiwyQkFBNkI7QUFDN0Isb0NBQXFDO0FBRXJDLDJDQUEyQztBQUMzQyxHQUFHLENBQUMsVUFBVSxDQUFDLElBQUksQ0FBQyxJQUFJLEdBQUcsV0FBSSxDQUFDLFlBQU8sRUFBRSxFQUFFLGNBQWMsRUFBRSxNQUFNLEVBQUUsVUFBVSxFQUFFLEdBQUcsQ0FBQyxVQUFVLENBQUMsSUFBSSxDQUFDLFFBQVEsQ0FBQyxDQUFDO0FBRTdHLElBQUksYUFBNEIsQ0FBQztBQUNqQyxJQUFJLFVBQXlCLENBQUM7QUFFOUIsU0FBUyxnQkFBZ0I7SUFDckIsVUFBVSxHQUFHLElBQUksd0JBQWEsQ0FBQztRQUMzQixLQUFLLEVBQUUsSUFBSTtRQUNYLE1BQU0sRUFBRSxHQUFHO1FBQ1gsS0FBSyxFQUFFLEtBQUs7UUFDWixJQUFJLEVBQUUsS0FBSztRQUNYLFdBQVcsRUFBRSxJQUFJO1FBQ2pCLFNBQVMsRUFBRSxLQUFLO1FBQ2hCLGNBQWMsRUFBRSxLQUFLO1FBQ3JCLGFBQWEsRUFBRSxzQkFBc0I7UUFDckMsY0FBYyxFQUFFO1lBQ1osZUFBZSxFQUFFLElBQUk7WUFDckIsa0JBQWtCLEVBQUUsSUFBSTtTQUMzQjtLQUNKLENBQUMsQ0FBQztJQUVILFVBQVUsQ0FBQyxFQUFFLENBQUMsT0FBTyxFQUFFLEdBQUcsRUFBRSxDQUFDLFVBQVUsQ0FBQyxrQkFBa0IsRUFBRSxDQUFDLENBQUM7SUFDOUQsVUFBVSxDQUFDLFdBQVcsQ0FBQyxFQUFFLENBQUMsaUJBQWlCLEVBQUUsR0FBRyxFQUFFLENBQUMsVUFBVSxDQUFDLFdBQVcsQ0FBQyxhQUFhLEVBQUUsQ0FBQyxDQUFDO0lBQzNGLFVBQVUsQ0FBQyxFQUFFLENBQUMsUUFBUSxFQUFFLEdBQUcsRUFBRSxHQUFHLFVBQVUsR0FBRyxJQUFJ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