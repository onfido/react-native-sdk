/**
 * This script adds the values from colors.json to a colors.xml resource for android
 *
 * Note: This script was written to work with ES5
 */
const fs = require('fs');
try {
  if (fs.existsSync('../../../colors.json')) {
    fs.readFile('../../../colors.json', 'utf8', function (err, data) {
      let colors = JSON.parse(data)

      // Explictly check and create needed directories to support older js versions
      if (!fs.existsSync('android')) {
        fs.mkdirSync('android')
      }
      if (!fs.existsSync('android/src')) {
        fs.mkdirSync('android/src')
      }
      if (!fs.existsSync('android/src/main')) {
        fs.mkdirSync('android/src/main')
      }
      if (!fs.existsSync('android/src/main/res')) {
        fs.mkdirSync('android/src/main/res')
      }
      if (!fs.existsSync('android/src/main/res/values')) {
        fs.mkdirSync('android/src/main/res/values')
      }

      fs.writeFile('android/src/main/res/values/colors.xml', generateFileContent(colors), function (e) {
        if (e != null) {
          console.log('\nAn error occured while trying to update colors:\n' + e + '\n')
        } else {
          console.log("\nColors were successfully updated\n")
        }
      })
    });
  } else {
    console.log('\nNo colors.json was found. Ensure it is at the same level as your node-modules directory\n')
  }
} catch (e) {
  console.log(e)
}

function generateFileContent(colors) {
  let fileContent = '<?xml version="1.0" encoding="utf-8"?>\n'
  fileContent += '<resources>\n'
  Object.keys(colors).forEach((color) => {
    let keyName = color;
    switch (keyName) {
      case 'onfidoPrimaryColor':
        keyName = 'onfidoPrimaryButtonColor'
        break
      case 'onfidoAndroidStatusBarColor':
        keyName = 'onfidoColorPrimary'
        break
      case 'onfidoAndroidToolBarColor':
        keyName = 'onfidoColorPrimaryDark'
        break
    }

    if (color !== 'onfidoIosSupportDarkMode') {
      fileContent += '\t<color name=\"'
      fileContent += keyName
      fileContent += "\">"
      fileContent += colors[color]
      fileContent += "</color>\n"
    }
  })
  fileContent += "</resources>"
  return fileContent
}
