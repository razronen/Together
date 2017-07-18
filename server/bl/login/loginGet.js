var mongoose = require('mongoose');
var userModel = mongoose.model('user');
var potentialUserModel = mongoose.model('potential_user');
var sendmail = require('sendmail')();

/**
 * Checks if an email like this already belongs to an existsing user.
 * @param _email - to check.
 * @param next - route function.
 */
exports.emailExists = function(_email,next) {
    userModel.find({email: _email}, function(err, data){
        if(err || data==undefined) next(err,data);
        else if(data.length==0){
            next(err,"not_exists");
        } else {
            next(err,"exists");
        }
    })
}

/**
 * Check if this deviceID is already approved by the apap
 * @param _code - deviceId
 * @param next - route function.
 */
exports.isApproved = function(_code,next) {
    potentialUserModel.findOne({code: _code}, function(err, data){
        if(err || data==undefined) next(err,"")
        else if (data.length==0) next(err,"not approved");
        else next(err, data);
    })
}

/**
 * authenticating a login request
 * @param _email - of user
 * @param _pass - of user.
 * @param next - route function.
 */
exports.checkLogin = function(_email,_pass,next){
    userModel.findOne({email: _email, pass: _pass}, function(err,data){
        if(err) next(err,data);
        else next(err,data);
    });
}
var _ejs = require('ejs');
var fs = require('fs');

var nodemailer = require("nodemailer");

var FROM_ADDRESS = 'togther.app@gmail.com';
var TO_ADDRESS = 'razronen1@gmail.com';

// create reusable transport method (opens pool of SMTP connections)
var smtpTransport = nodemailer.createTransport("SMTP",{
    service: "Gmail",
    auth: {
        user: "togther.app@gmail.com",
        pass: "door12345"
    }
});

/**
 * Send mail *for 'forgot your password'*
 * @param toAddress - whom to send
 * @param subject - title
 * @param content
 * @param next
 */
var sendMailobj = function(toAddress, subject, content, next){

    var mailOptions = {
        from: "togther.app@gmail.com",
        to: toAddress,
        replyTo: "togther.app@gmail.com",
        subject: subject,
        html: content
    };

    smtpTransport.sendMail(mailOptions, next);
};

/**
 * Mail sending procedure.
 * @param req
 * @param res
 */
exports.index = function(req, res){
    // res.render('index', { title: 'Express' });

    // specify jade template to load
    var template = process.cwd() + '/views/index.ejs';

    // get template from file system
    fs.readFile(template, 'utf8', function(err, file){
        if(err){
            //handle errors
            return res.send('ERROR!');
        }
        else {
            //compile jade template into function
            var compiledTmpl = _ejs.compile(file, {filename: template});
            // set context to be used in template
            var context = {title: 'Express'};
            // get html back as a string with the context applied;
            var html = compiledTmpl(context);
            sendMail(TO_ADDRESS, 'test', html, function(err, response){
                if(err){
                    return res.send('ERROR');
                }
                res.send("Email sent!");
            });
        }
    });
};

/**
 * Oredering a mail sending activity.
 * @param _email
 * @param _code - deviceId
 * @param next - route function.
 */
exports.sendPasswordToMail = function(_email, _code,  next){
    userModel.findOne({email: _email, deviceId: _code}, function(err, data) {
        if(err || data==undefined) next(err,data);
        else {
            sendMailobj(_email,"שחזור סיסמא",  "הסיסמא שלך היא: " + data.pass,function(err,data){
                next(null,"");
            })
        }

    });


}