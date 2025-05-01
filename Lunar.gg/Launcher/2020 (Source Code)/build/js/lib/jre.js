/* MIT License
 *
 * Copyright (c) 2016 schreiben
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
"use strict";
(function () {
    const os = require('os');
    const fs = require('fs');
    const path = require('path');
    const rimraf = require('rimraf');
    const zlib = require('zlib');
    const tar = require('tar-fs');
    const process = require('process');
    const request = require('request');
    const child_process = require('child_process');
    const { OFFLINE_JRE_DIRECTORY } = require('../constants');
    const major_version = '8';
    const update_number = '131';
    const build_number = '11';
    const hash = 'd54c1d3a095b4ff2b6607d096fa80163';
    const version = major_version + 'u' + update_number;
    const jreDir = exports.jreDir = () => OFFLINE_JRE_DIRECTORY;
    const fail = reason => {
        console.error(reason);
        process.exit(1);
    };
    var _arch = os.arch();
    switch (_arch) {
        case 'x64': break;
        case 'ia32':
            _arch = 'i586';
            break;
        default:
            fail('unsupported architecture: ' + _arch);
    }
    const arch = exports.arch = () => _arch;
    var _platform = os.platform();
    var _driver;
    switch (_platform) {
        case 'darwin':
            _platform = 'macosx';
            _driver = ['Contents', 'Home', 'bin', 'java'];
            break;
        case 'win32':
            _platform = 'windows';
            _driver = ['bin', 'javaw.exe'];
            break;
        case 'linux':
            _driver = ['bin', 'java'];
            break;
        default:
            fail('unsupported platform: ' + _platform);
    }
    const platform = exports.platform = () => _platform;
    const getDirectories = dirPath => fs.readdirSync(dirPath).filter(file => fs.statSync(path.join(dirPath, file)).isDirectory());
    const driver = exports.driver = () => {
        var jreDirs = getDirectories(jreDir());
        if (jreDirs.length < 1)
            fail('no jre found in archive');
        var d = _driver.slice();
        d.unshift(jreDirs[0]);
        d.unshift(jreDir());
        return path.join.apply(path, d);
    };
    const getArgs = exports.getArgs = (classpath, classname, args) => {
        args = (args || []).slice();
        classpath = classpath || [];
        args.unshift(classname);
        args.unshift(classpath.join(platform() === 'windows' ? ';' : ':'));
        args.unshift('-cp');
        return args;
    };
    const spawn = exports.spawn =
        (classpath, classname, args, options) => child_process.spawn(driver(), getArgs(classpath, classname, args), options);
    const spawnSync = exports.spawnSync =
        (classpath, classname, args, options) => child_process.spawnSync(driver(), getArgs(classpath, classname, args), options);
    const smoketest = exports.smoketest = () => spawnSync(['resources'], 'Smoketest', [], { encoding: 'utf8' })
        .stdout.trim() === 'No smoke!';
    const url = exports.url = () => 'https://download.oracle.com/otn-pub/java/jdk/' +
        version + '-b' + build_number + '/' + hash +
        '/jre-' + version + '-' + platform() + '-' + arch() + '.tar.gz';
    const install = exports.install = (callback, progressCallback) => {
        var urlStr = url();
        console.log("Downloading from: ", urlStr);
        callback = callback || (() => { });
        progressCallback(0);
        rimraf.sync(jreDir());
        request
            .get({
            url: url(),
            rejectUnauthorized: false,
            agent: false,
            headers: {
                connection: 'keep-alive',
                'Cookie': 'gpw_e24=http://www.oracle.com/; oraclelicense=accept-securebackup-cookie'
            }
        })
            .on('response', res => {
            var len = parseInt(res.headers['content-length'], 10);
            var done = 0;
            res.on('data', chunk => {
                done += chunk.length;
                progressCallback(done / len);
            });
        })
            .on('error', err => {
            console.log(`problem with request: ${err.message}`);
            callback(err);
        })
            .on('end', () => {
            try {
                if (smoketest())
                    callback();
                else
                    callback("Smoketest failed.");
            }
            catch (err) {
                callback(err);
            }
        })
            .pipe(zlib.createUnzip())
            .pipe(tar.extract(jreDir()));
    };
})();
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoianJlLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL2pzL2xpYi9qcmUuanMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztHQXFCRztBQUVILFlBQVksQ0FBQztBQUViLENBQUM7SUFFRyxNQUFNLEVBQUUsR0FBRyxPQUFPLENBQUMsSUFBSSxDQUFDLENBQUM7SUFDekIsTUFBTSxFQUFFLEdBQUcsT0FBTyxDQUFDLElBQUksQ0FBQyxDQUFDO0lBQ3pCLE1BQU0sSUFBSSxHQUFHLE9BQU8sQ0FBQyxNQUFNLENBQUMsQ0FBQztJQUM3QixNQUFNLE1BQU0sR0FBRyxPQUFPLENBQUMsUUFBUSxDQUFDLENBQUM7SUFDakMsTUFBTSxJQUFJLEdBQUcsT0FBTyxDQUFDLE1BQU0sQ0FBQyxDQUFDO0lBQzdCLE1BQU0sR0FBRyxHQUFHLE9BQU8sQ0FBQyxRQUFRLENBQUMsQ0FBQztJQUM5QixNQUFNLE9BQU8sR0FBRyxPQUFPLENBQUMsU0FBUyxDQUFDLENBQUM7SUFDbkMsTUFBTSxPQUFPLEdBQUcsT0FBTyxDQUFDLFNBQVMsQ0FBQyxDQUFDO0lBQ25DLE1BQU0sYUFBYSxHQUFHLE9BQU8sQ0FBQyxlQUFlLENBQUMsQ0FBQztJQUMvQyxNQUFNLEVBQUUscUJBQXFCLEVBQUUsR0FBRyxPQUFPLENBQUMsY0FBYyxDQUFDLENBQUM7SUFFMUQsTUFBTSxhQUFhLEdBQUcsR0FBRyxDQUFDO0lBQzFCLE1BQU0sYUFBYSxHQUFHLEtBQUssQ0FBQztJQUM1QixNQUFNLFlBQVksR0FBRyxJQUFJLENBQUM7SUFDMUIsTUFBTSxJQUFJLEdBQUcsa0NBQWtDLENBQUM7SUFDaEQsTUFBTSxPQUFPLEdBQUcsYUFBYSxHQUFHLEdBQUcsR0FBRyxhQUFhLENBQUM7SUFFcEQsTUFBTSxNQUFNLEdBQUcsT0FBTyxDQUFDLE1BQU0sR0FBRyxHQUFHLEVBQUUsQ0FBQyxxQkFBcUIsQ0FBQztJQUU1RCxNQUFNLElBQUksR0FB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