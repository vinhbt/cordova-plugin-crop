/* global cordova */
// var crop = module.exports = function cropImage (success, fail, image, options) {
//   options = options || {}
//   options.quality = options.quality || 100
//   return cordova.exec(success, fail, 'CropPlugin', 'cropImage', [image, options])
// }
//
// module.exports.promise = function cropAsync (image, options) {
//   return new Promise(function (resolve, reject) {
//     crop(resolve, reject, image, options)
//   })
// }
var argscheck = require('cordova/argscheck');
var exec = require('cordova/exec');

var cropPlugin = {};

cropPlugin.cropImage =  function cropImage(success, fail, image, options) {
        options = options || {};
        options.quality = options.quality || 100;
        options.imgWidth = options.imgWidth || -1;
        return exec(success, fail, 'CropPlugin', 'cropImage', [image, options])
}
cropPlugin.promiseImage =  function promiseImage(image, options) {
        return new Promise(function (resolve, reject) {
            options = options || {};
            options.quality = options.quality || 100;
            options.imgWidth = options.imgWidth || -1;
            return exec(resolve, reject, 'CropPlugin', 'cropImage', [image, options])
        })
}

if (window) {
    window.cropPlugin = cropPlugin;
}

module.exports = cropPlugin;

