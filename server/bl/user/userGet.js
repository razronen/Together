var mongoose = require('mongoose');
var response = mongoose.model('response');
var user = mongoose.model('user');
var time_statistics = mongoose.model('time_statistics');

/**
 * Get children by name
 * @param _name - name of child
 * @param next - route function.
 * @constructor
 */
exports.GetChildrenByName = function (_name, next) {
    user.find({name: _name}, function (err, data) {
        if (err) next(err)
        else next(err, data);
    })
};

/**
 * Get the login last time of this user
 * @param _id - of user
 * @param _time - from time
 * @param next - route function.
 */
exports.getLoginTime = function (_id, _time, next) {
    var conditions = {id: _id}
        , update = {lastlogin: _time}
        , options = {multi: true};
    user.update(conditions, update, options, function (err, data) {
        next();
    });
};

/**
 * Get user object.
 * @param _id - of object
 * @param next - route function.
 */
exports.getUserDetails = function (_id, next) {
    user.findOne({id: _id}, function (err, data) {
        if (err) next(err)
        else next(err, data);
    })
};

/**
 * updating day statistic that a user has deleted the app.
 * @param next
 */
exports.getDestroyApplication = function (next) {
    var destroyApp = {user_delete: (new Date).getTime()};
    var DestroyApp = time_statistics(destroyApp);
    DestroyApp.save(function (err, data) {
        next(err, data);
    });
};