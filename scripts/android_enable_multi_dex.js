/**
 * This script adds "multiDexEnabled true" to android/app/build.gradle.
 * 
 * android {
 *   defaultConfig {
 *     multiDexEnabled true
 *   }
 * }
 * 
 * Note: This script was written to work with ES5
 */

var fs = require('fs');

var targetConfigName = 'multiDexEnabled';
var targetConfigValue = 'true';
var targetConfig = targetConfigName + ' ' + targetConfigValue;
var targetCurrentPath = '../../../';
var targetFile = 'android/app/build.gradle';
var targetFileWithFullPath = targetCurrentPath + targetFile;
var contents = fs.readFileSync(targetFileWithFullPath, 'utf8');
var targetConfigExists = contents.indexOf(targetConfig) != -1;
var targetConfigNameExists = contents.indexOf(targetConfigName) != -1;

function indexOfOrExit(contents, target, startIndex) {
    var index = contents.indexOf(target, startIndex);
    if (index == -1) {
        console.warn('Warning: could not add', targetConfig, 'to', targetFile);
        process.exit(1);
    }
    return index;
}

if (!targetConfigExists && !targetConfigNameExists) {
    var androidIndex = indexOfOrExit(contents, "android {", 0);
    var defaultConfigIndex = indexOfOrExit(contents, "defaultConfig", androidIndex);
    var defaultConfigIndexBraceIndex = indexOfOrExit(contents, "{", defaultConfigIndex) + 1;
    var contentsStart = contents.substring(0,defaultConfigIndexBraceIndex);
    var contentsToInsert = '\n        ' + targetConfig + '\n';
    var contentsEnd = contents.substring(defaultConfigIndexBraceIndex);
    var contentsUpdated = contentsStart + contentsToInsert + contentsEnd;
    console.log('Updated:', targetConfig, 'was added to', targetFile);
    fs.writeFileSync(targetFileWithFullPath, contentsUpdated);
} else if (!targetConfigExists && targetConfigNameExists) {
    console.log('Warning:', targetConfigName, 'is already in', targetFile, 'but is set to a different value');
} else {
    console.log('Verified:', targetConfig, 'is already in', targetFile);
}