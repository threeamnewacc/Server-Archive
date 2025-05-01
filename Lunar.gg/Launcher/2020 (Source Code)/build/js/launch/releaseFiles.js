"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.updateFileCache = exports.collectFileCacheHashes = exports.extractNatives = void 0;
const path_1 = require("path");
const fs_1 = require("fs");
const os_1 = require("os");
const constants_1 = require("../constants");
const sha1File = require('sha1-file');
const request = require('request');
const unzipper = require('unzipper');
const rimraf = require('rimraf');
async function extractNatives(basePath) {
    let nativeName = 'unknown';
    switch (os_1.type()) {
        case 'Darwin':
            nativeName = 'natives_macos';
            break;
        case 'win32':
        case 'Windows_NT':
            nativeName = 'natives_win';
            break;
        default:
            nativeName = 'natives_linux';
            break;
    }
    let sourceFile = path_1.join(basePath, nativeName);
    let targetDirectory = path_1.join(basePath, 'natives');
    return new Promise((resolve, reject) => {
        rimraf(targetDirectory, {}, (error) => {
            if (error) {
                reject({
                    short: 'Extract failed',
                    description: 'Failed to extract natives. Is the game already running?'
                });
                return;
            }
            fs_1.createReadStream(sourceFile)
                .pipe(unzipper.Extract({ path: targetDirectory }))
                .on('close', () => resolve());
        });
    });
}
exports.extractNatives = extractNatives;
async function collectFileCacheHashes(basePath) {
    const hashes = {};
    ['assets_lunar', 'assets_minecraft', 'libraries', 'natives_win', 'natives_macos', 'natives_linux', 'client'].forEach(fileName => {
        let file = path_1.join(basePath, fileName);
        // @ts-ignore
        hashes[fileName] = fs_1.existsSync(file) ? sha1File(file) : null;
    });
    return hashes;
}
exports.collectFileCacheHashes = collectFileCacheHashes;
async function updateFileCache(updates, basePath, progressCallback) {
    let remaining = Object.keys(updates).length;
    let total = remaining;
    return new Promise((resolve, _reject) => {
        if (remaining === 0) {
            resolve();
        }
        else {
            progressCallback('Updating', 'Downloading ' + (total - remaining) + '/' + total);
            for (let fileName in updates) {
                request
                    .get({
                    url: updates[fileName],
                    headers: {
                        'User-Agent': constants_1.USER_AGENT
                    }
                })
                    .pipe(fs_1.createWriteStream(path_1.join(basePath, fileName)))
                    .on('close', () => {
                    remaining--;
                    progressCallback('Updating', 'Downloading ' + (total - remaining + 1) + '/' + total);
                    if (remaining === 0) {
                        resolve();
                    }
                });
            }
        }
    });
}
exports.updateFileCache = updateFileCache;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoicmVsZWFzZUZpbGVzLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL2pzL2xhdW5jaC9yZWxlYXNlRmlsZXMudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsK0JBQTRCO0FBQzVCLDJCQUFxRTtBQUNyRSwyQkFBMEI7QUFDMUIsNENBQTBDO0FBRzFDLE1BQU0sUUFBUSxHQUFHLE9BQU8sQ0FBQyxXQUFXLENBQUMsQ0FBQztBQUN0QyxNQUFNLE9BQU8sR0FBRyxPQUFPLENBQUMsU0FBUyxDQUFDLENBQUM7QUFDbkMsTUFBTSxRQUFRLEdBQUcsT0FBTyxDQUFDLFVBQVUsQ0FBQyxDQUFDO0FBQ3JDLE1BQU0sTUFBTSxHQUFHLE9BQU8sQ0FBQyxRQUFRLENBQUMsQ0FBQztBQUUxQixLQUFLLFVBQVUsY0FBYyxDQUFDLFFBQWdCO0lBQ2pELElBQUksVUFBVSxHQUFHLFNBQVMsQ0FBQztJQUUzQixRQUFRLFNBQUksRUFBRSxFQUFFO1FBQ1osS0FBSyxRQUFRO1lBQ1QsVUFBVSxHQUFHLGVBQWUsQ0FBQztZQUM3QixNQUFNO1FBQ1YsS0FBSyxPQUFPLENBQUM7UUFDYixLQUFLLFlBQVk7WUFDYixVQUFVLEdBQUcsYUFBYSxDQUFDO1lBQzNCLE1BQU07UUFDVjtZQUNJLFVBQVUsR0FBRyxlQUFlLENBQUM7WUFDN0IsTUFBTTtLQUNiO0lBRUQsSUFBSSxVQUFVLEdBQUcsV0FBSSxDQUFDLFFBQVEsRUFBRSxVQUFVLENBQUMsQ0FBQztJQUM1QyxJQUFJLGVBQWUsR0FBRyxXQUFJLENBQUMsUUFBUSxFQUFFLFNBQVMsQ0FBQyxDQUFDO0lBRWhELE9BQU8sSUFBSSxPQUFPLENBQUMsQ0FBQyxPQUFPLEVBQUUsTUFBTSxFQUFFLEVBQUU7UUFDbkMsTUFBTSxDQUFDLGVBQWUsRUFBRSxFQUFFLEVBQUUsQ0FBQyxLQUFVLEVBQUUsRUFBRTtZQUN2QyxJQUFJLEtBQUssRUFBRTtnQkFDUCxNQUFNLENBQUM7b0JBQ0gsS0FBSyxFQUFF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