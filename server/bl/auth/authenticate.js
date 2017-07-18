var mongoose = require('mongoose');
var userModel = mongoose.model('user');

/**
 * Used to authenticate the connection by email and password of user.
 * @param _email - email
 * @param _pass - password
 * @param next - route function
 * @returns {boolean} - if authernticated or not.
 */
exports.authen = function (_email, _pass, next) {
    if (email == undefined || pass == undefined) return false;
    userModel.find({email: _email, pass: _pass}, function (err, data) {
        if (err || data == undefined) next(false);
        else next(true);
    })
}