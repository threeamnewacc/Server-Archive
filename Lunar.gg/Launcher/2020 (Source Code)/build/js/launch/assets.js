"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.downloadAssets = void 0;
// @ts-ignore
const fs = require("fs");
const path = require("path");
// @ts-ignore
const sha1File = require("sha1-file");
// @ts-ignore
const request = require("request");
const log = require("electron-log");
const settings_1 = require("../settings");
// TODO
async function downloadAssets(info) {
    log.info('Making assets request...');
    return new Promise((resolve, reject) => {
        request.get(info.url, (error, response, body) => {
            if (error) {
                reject(error);
                return;
            }
            log.info('Received assets response, status is ' + response.statusCode);
            let assetsPath = path.join(settings_1.getLaunchDirectory(), 'assets');
            let objects = JSON.parse(body).objects;
            let remaining = Object.keys(objects).length;
            // Save this file in the indexes
            fs.mkdirSync(path.join(assetsPath, 'indexes'), { recursive: true });
            fs.writeFileSync(path.join(assetsPath, 'indexes', info.id + '.json'), body);
            // Download individual objects
            for (let fileName in objects) {
                let expectedHash = objects[fileName].hash;
                let parentPath = path.join(assetsPath, 'objects', expectedHash.substring(0, 2));
                let filePath = path.join(parentPath, expectedHash);
                sha1File(filePath, (shaError, actualHash) => {
                    if (shaError || actualHash !== expectedHash) {
                        log.info('Need to download ' + fileName + "... Expected: " + expectedHash + ", actual: " + actualHash);
                        fs.mkdirSync(parentPath, { recursive: true });
                        request
                            .get('https://resources.download.minecraft.net/' + expectedHash.substring(0, 2) + '/' + expectedHash)
                            .on('error', function (err) {
                            log.info('Failed to download assets file: ' + err);
                        })
                            .pipe(fs.createWriteStream(filePath))
                            .on('error', function (err) {
                            log.info('Failed to write assets file: ' + err);
                        })
                            .on('close', () => {
                            remaining--;
                            log.info('Downloaded ' + fileName + ', remaining: ' + remaining);
                            if (remaining === 0)
                                resolve();
                        });
                    }
                    else {
                        remaining--;
                        if (remaining === 0)
                            resolve();
                    }
                });
            }
        });
    });
}
exports.downloadAssets = downloadAssets;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiYXNzZXRzLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL2pzL2xhdW5jaC9hc3NldHMudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsYUFBYTtBQUNiLHlCQUEwQjtBQUMxQiw2QkFBOEI7QUFDOUIsYUFBYTtBQUNiLHNDQUF1QztBQUN2QyxhQUFhO0FBQ2IsbUNBQW9DO0FBQ3BDLG9DQUFxQztBQUNyQywwQ0FBaUQ7QUFHakQsT0FBTztBQUNBLEtBQUssVUFBVSxjQUFjLENBQUMsSUFBZ0I7SUFDakQsR0FBRyxDQUFDLElBQUksQ0FBQywwQkFBMEIsQ0FBQyxDQUFDO0lBRXJDLE9BQU8sSUFBSSxPQUFPLENBQUMsQ0FBQyxPQUFPLEVBQUUsTUFBTSxFQUFFLEVBQUU7UUFDbkMsT0FBTyxDQUFDLEdBQUcsQ0FBQyxJQUFJLENBQUMsR0FBRyxFQUFFLENBQUMsS0FBVSxFQUFFLFFBQWEsRUFBRSxJQUFTLEVBQUUsRUFBRTtZQUMzRCxJQUFJLEtBQUssRUFBRTtnQkFDUCxNQUFNLENBQUMsS0FBSyxDQUFDLENBQUM7Z0JBQ2QsT0FBTzthQUNWO1lBRUQsR0FBRyxDQUFDLElBQUksQ0FBQyxzQ0FBc0MsR0FBRyxRQUFRLENBQUMsVUFBVSxDQUFDLENBQUM7WUFFdkUsSUFBSSxVQUFVLEdBQUcsSUFBSSxDQUFDLElBQUksQ0FBQyw2QkFBa0IsRUFBRSxFQUFFLFFBQVEsQ0FBQyxDQUFDO1lBQzNELElBQUksT0FBTyxHQUFHLElBQUksQ0FBQyxLQUFLLENBQUMsSUFBSSxDQUFDLENBQUMsT0FBTyxDQUFDO1lBQ3ZDLElBQUksU0FBUyxHQUFHLE1BQU0sQ0FBQyxJQUFJLENBQUMsT0FBTyxDQUFDLENBQUMsTUFBTSxDQUFDO1lBRTVDLGdDQUFnQztZQUNoQyxFQUFFLENBQUMsU0FBUyxDQUFDLElBQUksQ0FBQyxJQUFJLENBQUMsVUFBVSxFQUFFLFNBQVMsQ0FBQyxFQUFFLEVBQUUsU0FBUyxFQUFFLElBQUksRUFBRSxDQUFDLENBQUM7WUFDcEUsRUFBRSxDQUFDLGFBQWEsQ0FBQyxJQUFJLENBQUMsSUFBSSxDQUFDLFVBQVUsRUFBRSxTQUFTLEVBQUUsSUFBSSxDQUFDLEVBQUUsR0FBRyxPQUFPLENBQUMsRUFBRSxJQUFJLENBQUMsQ0FBQztZQUU1RSw4QkFBOEI7WUFDOUIsS0FBSyxJQUFJLFFBQVEsSUFBSSxPQUFPLEVBQUU7Z0JBQzFCLElBQUksWUFBWSxHQUFHLE9BQU8sQ0FBQyxRQUFRLENBQUMsQ0FBQyxJQUFJLENBQUM7Z0JBQzFDLElBQUksVUFBVSxHQUFHLElBQUksQ0FBQyxJQUFJLENBQUMsVUFBVSxFQUFFLFNBQVMsRUFBRSxZQUFZLENBQUMsU0FBUyxDQUFDLENBQUMsRUFBRSxDQUFDLENBQUMsQ0FBQyxDQUFDO2dCQUNoRixJQUFJLFFBQVEsR0FBRyxJQUFJLENBQUMsSUFBSSxDQUFDLFVBQVUsRUFBRSxZQUFZLENBQUMsQ0FBQztnQkFFbkQsUUFBUSxDQUFDLFFBQVEsRUFBRSxDQUFDLFFBQWEsRUFBRSxVQUFlLEVBQUUsRUFBRTtvQkFDbEQsSUFBSSxRQUFRLElBQUksVUFBVSxLQUFLLFlBQVksRUFBRTt3QkFDekMsR0FBRyxDQUFDLElBQUksQ0FBQyxtQkFBbUIsR0FBRyxRQUFRLEdBQUcsZ0JBQWdCLEdBQUcsWUFBWSxHQUFHLFlBQVksR0FBRyxVQUFVLENBQUMsQ0FBQzt3QkFFdkcsRUFBRSxDQUFDLFNBQVMsQ0FBQyxVQUFVLEVBQUUsRUFBRSxTQUFTLEVBQUUsSUFBSSxFQUFFLENBQUMsQ0FBQzt3QkFDOUMsT0FBTzs2QkFDRixHQUFHLENBQUMsMkNBQTJDLEdBQUcsWUFBWSxDQUFDLFNBQVMsQ0FBQyxDQUFDLEVBQUUsQ0FBQyxDQUFDLEdBQUcsR0FBRyxHQUFHLFlBQVksQ0FBQzs2QkFDcEcsRUFBRSxDQUFDLE9BQU8sRUFBRSxVQUFVLEdBQVE7NEJBQzNCLEdBQUcsQ0FBQyxJQUFJLENBQUMsa0NBQWtDLEdBQUcsR0FBRyxDQUFDLENBQUM7d0JBQ3ZELENBQUMsQ0FBQzs2QkFDRCxJQUFJLENBQUMsRUFBRSxDQUFDLGlCQUFpQixDQUFDLFFBQVEsQ0FBQyxDQUFDOzZCQUNwQyxFQUFFLENBQUMsT0FBTyxFQUFFLFVBQVMsR0FBUTs0QkFDMUIsR0FBRyxDQUFDLElBQUksQ0FBQywrQkFBK0IsR0FBRyxHQUFHLENBQUMsQ0FBQzt3QkFDcEQsQ0FBQyxDQUFDOzZCQUNELEVBQUUsQ0FBQyxPQUFPLEVBQUUsR0FBRyxFQUFFOzRCQUNkLFNBQVMsRUFBRSxDQUFDOzRCQUNaLEdBQUcsQ0FBQyxJQUFJLENBQUMsYUFBYSxHQUFHLFFBQVEsR0FBRyxlQUFlLEdBQUcsU0FBUyxDQUFDLENBQUM7NEJBQ2pFLElBQUksU0FBUyxLQUFLLENBQUM7Z0NBQUUsT0FBTyxFQUFFLENBQUM7d0JBQ25DLENBQUMsQ0FBQyxDQUFDO3FCQUNWO3lCQUFNO3dCQUNILFNBQVMsRUFBRSxDQUFDO3dCQUNaLElBQUksU0FBUyxLQUFLLENBQUM7NEJBQUUsT0FBTyxFQUFFLENBQUM7cUJBQ2xDO2dCQUNMLENBQUMsQ0FBQyxDQUFDO2FBQ047UUFDTCxDQUFDLENBQUMsQ0FBQztJQUNQLENBQUMsQ0FBQyxDQUFDO0FBQ1AsQ0FBQztBQXJERCx3Q0FxREMifQ==