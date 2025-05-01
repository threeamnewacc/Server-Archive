"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const electron_updater_1 = require("electron-updater");
const electron_1 = require("electron");
const ProgressBar = require('electron-progressbar');
electron_updater_1.autoUpdater.logger = require('electron-log').scope('autoUpdater');
electron_updater_1.autoUpdater.requestHeaders = {
    'User-Agent': 'Lunar Client Launcher v' + electron_1.app.getVersion()
};
electron_updater_1.autoUpdater.checkForUpdates();
let progressBar;
electron_updater_1.autoUpdater.on('update-available', (info) => {
    progressBar = new ProgressBar({
        title: 'Launcher Update',
        text: 'Downloading launcher update v' + info.version,
        indeterminate: false,
        value: 0,
        detail: 'Starting update...',
        browserWindow: {
            webPreferences: {
                nodeIntegration: true
            }
        }
    });
});
electron_updater_1.autoUpdater.signals.progress((progress) => {
    let percent = Math.round(progress.percent);
    progressBar.value = percent;
    progressBar.detail = percent + '% downloaded (' + progress.bytesPerSecond + ' bytes/sec)';
});
electron_updater_1.autoUpdater.on('update-downloaded', () => {
    progressBar.setCompleted();
    let installDelay = new ProgressBar({
        title: 'Launcher Update',
        text: 'Waiting to install...',
        indeterminate: true,
        detail: 'Waiting to install...',
        browserWindow: {
            webPreferences: {
                nodeIntegration: true
            }
        }
    });
    setTimeout(() => {
        installDelay.setCompleted();
        electron_updater_1.autoUpdater.quitAndInstall(false, true);
    }, 10000);
});
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiYXV0b1VwZGF0ZXIuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi9zcmMvYXV0b1VwZGF0ZXIudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7QUFBQSx1REFBMkQ7QUFDM0QsdUNBQStCO0FBQy9CLE1BQU0sV0FBVyxHQUFHLE9BQU8sQ0FBQyxzQkFBc0IsQ0FBQyxDQUFDO0FBRXBELDhCQUFXLENBQUMsTUFBTSxHQUFHLE9BQU8sQ0FBQyxjQUFjLENBQUMsQ0FBQyxLQUFLLENBQUMsYUFBYSxDQUFDLENBQUM7QUFDbEUsOEJBQVcsQ0FBQyxjQUFjLEdBQUc7SUFDekIsWUFBWSxFQUFFLHlCQUF5QixHQUFHLGNBQUcsQ0FBQyxVQUFVLEVBQUU7Q0FDN0QsQ0FBQztBQUVGLDhCQUFXLENBQUMsZUFBZSxFQUFFLENBQUM7QUFFOUIsSUFBSSxXQUFnQixDQUFDO0FBRXJCLDhCQUFXLENBQUMsRUFBRSxDQUFDLGtCQUFrQixFQUFFLENBQUMsSUFBZ0IsRUFBRSxFQUFFO0lBQ3BELFdBQVcsR0FBRyxJQUFJLFdBQVcsQ0FBQztRQUMxQixLQUFLLEVBQUUsaUJBQWlCO1FBQ3hCLElBQUksRUFBRSwrQkFBK0IsR0FBRyxJQUFJLENBQUMsT0FBTztRQUNwRCxhQUFhLEVBQUUsS0FBSztRQUNwQixLQUFLLEVBQUUsQ0FBQztRQUNSLE1BQU0sRUFBRSxvQkFBb0I7UUFDNUIsYUFBYSxFQUFFO1lBQ1gsY0FBYyxFQUFFO2dCQUNaLGVBQWUsRUFBRSxJQUFJO2FBQ3hCO1NBQ0o7S0FDSixDQUFDLENBQUM7QUFDUCxDQUFDLENBQUMsQ0FBQztBQUVILDhCQUFXLENBQUMsT0FBTyxDQUFDLFFBQVEsQ0FBQyxDQUFDLFFBQVEsRUFBRSxFQUFFO0lBQ3RDLElBQUksT0FBTyxHQUFHLElBQUksQ0FBQyxLQUFLLENBQUMsUUFBUSxDQUFDLE9BQU8sQ0FBQyxDQUFDO0lBRTNDLFdBQVcsQ0FBQyxLQUFLLEdBQUcsT0FBTyxDQUFDO0lBQzVCLFdBQVcsQ0FBQyxNQUFNLEdBQUcsT0FBTyxHQUFHLGdCQUFnQixHQUFHLFFBQVEsQ0FBQyxjQUFjLEdBQUcsYUFBYSxDQUFDO0FBQzlGLENBQUMsQ0FBQyxDQUFDO0FBRUgsOEJBQVcsQ0FBQyxFQUFFLENBQUMsbUJBQW1CLEVBQUUsR0FBRyxFQUFFO0lBQ3JDLFdBQVcsQ0FBQyxZQUFZLEVBQUUsQ0FBQztJQUUzQixJQUFJLFlBQVksR0FBRyxJQUFJLFdBQVcsQ0FBQztRQUMvQixLQUFLLEVBQUUsaUJBQWlCO1FBQ3hCLElBQUksRUFBRSx1QkFBdUI7UUFDN0IsYUFBYSxFQUFFLElBQUk7UUFDbkIsTUFBTSxFQUFFLHVCQUF1QjtRQUMvQixhQUFhLEVBQUU7WUFDWCxjQUFjLEVBQUU7Z0JBQ1osZUFBZSxFQUFFLElBQUk7YUFDeEI7U0FDSjtLQUNKLENBQUMsQ0FBQztJQUVILFVBQVUsQ0FBQyxHQUFHLEVBQUU7UUFDWixZQUFZLENBQUMsWUFBWSxFQUFFLENBQUM7UUFDNUIsOEJBQVcsQ0FBQyxjQUFjLENBQUMsS0FBSyxFQUFFLElBQUksQ0FBQyxDQUFBO0lBQzNDLENBQUMsRUFBRSxLQUFLLENBQUMsQ0FBQztBQUNkLENBQUMsQ0FBQyxDQUFDIn0=