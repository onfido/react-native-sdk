/**
 * This script adds the bintray maven url to android/build.gradle.
 * 
 * allprojects {
 *   repositories {
 *     maven { url "https://dl.bintray.com/onfido/maven" }
 *   }
 * }
 * 
 * Note: This script was written to work with ES5
 */

var fs = require('fs');

var mavenUrl = 'https://dl.bintray.com/onfido/maven';
var targetCurrentPath = '../../../';
var targetFile = 'android/build.gradle';
var targetFileWithFullPath = targetCurrentPath + targetFile;

var contents = fs.readFileSync(targetFileWithFullPath, 'utf8');
var isLinked = contents.indexOf(mavenUrl) != -1;

function indexOfOrExit(contents, target, startIndex) {
    var index = contents.indexOf(target, startIndex);
    if (index == -1) {
        console.warn('Warning: could not add', mavenUrl, 'to', targetFile);
        process.exit(1);
    }
    return index;
}

if (!isLinked) {
    var allprojectsIndex = indexOfOrExit(contents, "allprojects", 0);
    var repositoriesIndex = indexOfOrExit(contents, "repositories", allprojectsIndex);
    var repositoriesBraceIndex = indexOfOrExit(contents, "{", repositoriesIndex) + 1;
    var contentsStart = contents.substring(0,repositoriesBraceIndex);
    var contentsToInsert = '\n        maven { url "' + mavenUrl + '" } \n';
    var contentsEnd = contents.substring(repositoriesBraceIndex);
    var contentsUpdated = contentsStart + contentsToInsert + contentsEnd;
    fs.writeFileSync(targetFileWithFullPath, contentsUpdated);
    console.log('Updated:', mavenUrl, 'was added to', targetFile);
} else {
    console.log('Verified:', mavenUrl, 'is already in', targetFile);
}