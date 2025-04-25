class AjaxForm {

    constructor(url, method, formId, responseId, validatorOptions, responseTypes, dataCall, responseCall) {
        var formElement = $('#' + formId);
        var responseElement = $('#' + responseId);
        var formValidator = validatorOptions === null ? null : formElement.validate(validatorOptions);

        if (responseTypes['fail'] === null) {
            responseTypes['fail'] = {
                'class': 'alert-danger',
                'message': 'Failed to process this request.'
            }
        }

        // hide response element
        responseElement.hide();

        // handle form submit
        formElement.submit(function (event) {
            event.preventDefault();

            responseElement.hide();
            responseElement.removeClass('alert-danger');
            responseElement.removeClass('alert-success');

            if (formValidator !== null && !formValidator.valid()) {
                formValidator.showErrors();
                return;
            }

            var current = new Date().getTime();
            var last = getPageAttribute('last_submit');

            if (last !== undefined && current < last + 3000) {
                responseElement.show();
                responseElement.addClass('alert-danger');
                responseElement.html('Please wait before trying to submit the form again.');
                return;
            }

            setPageAttribute('last_submit', current);

            $.ajax({
                method: method,
                url: url,
                data: dataCall(formElement),
                dataType: 'json'
            }).done(function (json) {
                if ('response' in json) {
                    var response = json['response'].toLowerCase();

                    if (responseTypes[response] !== null) {
                        var responseType = responseTypes[response];

                        responseElement.show();
                        responseElement.addClass(responseType['class']);
                        responseElement.html(responseType['message']);

                        responseCall(response, json);
                    }
                    else {
                        responseElement.show();
                        responseElement.addClass('alert-primary');
                        responseElement.html('Finished request but response not handled.');
                    }
                }
                else {
                    responseElement.show();
                    responseElement.addClass(responseTypes['fail']['class']);
                    responseElement.html(responseTypes['fail']['message']);
                }
            }).fail(function () {
                responseElement.show();
                responseElement.addClass(responseTypes['fail']['class']);
                responseElement.html(responseTypes['fail']['message']);
            });
        });
    }

}

class AjaxButton {

    constructor(url, method, buttonClass, responseId, responseTypes, dataCall, responseCall) {
        var buttonElement = $('.' + buttonClass);
        var responseElement = $('#' + responseId);

        if (responseTypes['fail'] === null) {
            responseTypes['fail'] = {
                'class': 'alert-danger',
                'message': 'Failed to process this request.'
            }
        }

        responseElement.hide();

        buttonElement.click(function (event) {
            event.preventDefault();

            responseElement.hide();
            responseElement.removeClass('alert-danger');
            responseElement.removeClass('alert-success');

            var current = new Date().getTime();
            var last = getPageAttribute('last_submit');

            if (last !== undefined && current < last + 3000) {
                responseElement.show();
                responseElement.addClass('alert-danger');
                responseElement.html('Please wait before trying attempting this action.');
                return;
            }

            setPageAttribute('last_submit', current);

            $.ajax({
                method: method,
                url: url,
                data: dataCall($(this)),
                dataType: 'json'
            }).done(function (json) {
                if ('response' in json) {
                    var response = json['response'].toLowerCase();

                    if (responseTypes[response] !== null) {
                        var responseType = responseTypes[response];

                        responseElement.show();
                        responseElement.addClass(responseType['class']);
                        responseElement.html(responseType['message']);

                        responseCall(response, json);
                    }
                    else {
                        responseElement.show();
                        responseElement.addClass('alert-primary');
                        responseElement.html('Finished request but response not handled.');
                    }
                }
                else {
                    responseElement.show();
                    responseElement.addClass(responseTypes['fail']['class']);
                    responseElement.html(responseTypes['fail']['message']);
                }
            }).fail(function () {
                responseElement.show();
                responseElement.addClass(responseTypes['fail']['class']);
                responseElement.html(responseTypes['fail']['message']);
            });
        });
    }

}

function setupLoginForm() {
    new AjaxForm('/login/', 'POST', 'login-form', 'login-response', {
        rules: {
            email: {
                required: true,
                minlength: 6
            },
            password: {
                required: true,
                minlength: 6
            }
        }
    }, {
        'success': {
            'class': 'alert-success',
            'message': 'You are now logged in. Redirecting you...'
        },
        'fail': {
            'class': 'alert-danger',
            'message': 'Failed to login. Check your credentials and try again.'
        }
    }, function (element) {
        return {
            'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
            'email': element.find('input[name="email"]').val(),
            'password': element.find('input[name="password"]').val()
        }
    }, function (response) {
        if (response === 'success') {
            setTimeout(function () {
                window.location.href = '/';
            }, 2000);
        }
    });
}

function setupRecoveryForm() {
    new AjaxForm('/forgot-password/', 'POST', 'reset-form', 'reset-response', {
        rules: {
            email_address: {
                required: true,
                email: true
            }
        }
    }, {
        'success': {
            'class': 'alert-success',
            'message': 'Check your email for instructions on how to reset your password.'
        },
        'missing-record': {
            'class': 'alert-danger',
            'message': 'An account with that email address could not be found.'
        },
        'unauthorized': {
            'class': 'alert-danger',
            'message': 'You are not allowed to submit forms to that page whilst logged in.'
        },
        'missing-fields': {
            'class': 'alert-danger',
            'message': 'Failed to send your password reset link.'
        },
        'fail': {
            'class': 'alert-danger',
            'message': 'Failed to send your password reset link.'
        }
    }, function (element) {
        return {
            'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
            'email_address': element.find('input[name="email_address"]').val()
        }
    }, function (response) {
    });
}

function setupRegisterForm() {
    new AjaxForm('/register/', 'POST', 'register-form', 'register-response', {
        rules: {
            email: {
                required: true,
                email: true
            },
            confirmation: {
                required: true
            },
            password: {
                required: true,
                minlength: 6
            },
            password_match: {
                required: true,
                minlength: 6
            }
        }
    }, {
        'success': {
            'class': 'alert-success',
            'message': 'Your account has been registered. Redirecting you...'
        },
        'already-registered': {
            'class': 'alert-danger',
            'message': 'That email address or Minecraft account is already linked to a Zonix account.'
        },
        'confirmation-not-found': {
            'class': 'alert-danger',
            'message': 'That confirmation ID is not linked to any account.'
        },
        'pair-non-match': {
            'class': 'alert-danger',
            'message': 'That email address and confirmation ID pair you provided does not match our records.'
        },
        'password-non-match': {
            'class': 'alert-danger',
            'message': 'Your passwords do not match.'
        },
        'api-fail': {
            'class': 'alert-danger',
            'message': 'Failed to register your account. Try again later.'
        },
        'fail': {
            'class': 'alert-danger',
            'message': 'Failed to register your account. Try again later.'
        }
    }, function (element) {
        return {
            'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
            'email_address': element.find('input[name="email"]').val(),
            'confirmation': element.find('input[name="confirmation"]').val(),
            'password': element.find('input[name="password"]').val(),
            'password_match': element.find('input[name="password_match"]').val()
        }
    }, function (response) {
    });
}

function setupManageForms() {
    new AjaxForm('/account/manage/change-password/', 'POST', 'change-password-form', 'change-password-response', {
        rules: {
            password_current: {
                required: true,
                minlength: 6
            },
            password_new: {
                required: true,
                minlength: 6
            },
            password_new_match: {
                required: true,
                minlength: 6
            }
        }
    }, {
        'success': {
            'class': 'alert-success',
            'message': 'You have changed your password.'
        },
        'invalid-old-password': {
            'class': 'alert-danger',
            'message': 'The password you entered does not match your current password.'
        },
        'matching-new-password': {
            'class': 'alert-danger',
            'message': 'The password you entered matches your current password.'
        },
        'fail': {
            'class': 'alert-danger',
            'message': 'Failed to change your password. Try again later.'
        }
    }, function (element) {
        return {
            'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
            'password_current': element.find('input[name="password_current"]').val(),
            'password_new': element.find('input[name="password_new"]').val(),
            'password_new_match': element.find('input[name="password_new_match"]').val()
        }
    }, function (response) {
    });

    new AjaxButton('/api/client/authentication/update', 'POST', 'update-btn', 'update-response', {
        'success': {
            'class': 'alert-success',
            'message': 'Your IP has been bound to a client whitelist.'
        },
        'fail': {
            'class': 'alert-danger',
            'message': 'Failed to update your IP. Are you already authorized?'
        }
    }, function (element) {
        return {
            'csrfmiddlewaretoken': getPageAttribute('csrf_token')
        }
    }, function (response) {
    });
}

function setupCreateThreadForm() {
    tinymce.init({
        selector: '#thread-textarea',
        code_dialog_height: 200,
        plugins: 'bbcode code',
        toolbar: 'undo redo | bold italic underline | code',
        content_css: [
            '//fonts.googleapis.com/css?family=Lato:300,300i,400,400i',
            '//www.tinymce.com/css/codepen.min.css'
        ],
        statusbar: false
    });

    new AjaxForm('/forums/t/c/' + getPageAttribute('forum_id') + '/', 'POST', 'new-thread-form', 'new-thread-response', {
        rules: {
            thread_title: {
                required: true,
                minlength: 3,
                maxlength: 80
            }
        }
    }, {
        'success': {
            'class': 'alert-success',
            'message': 'You created a new thread. Redirecting you...'
        },
        'denied': {
            'class': 'alert-danger',
            'message': 'You cannot post a reply right now. This may be the result of a banned account.'
        },
        'missing-fields': {
            'class': 'alert-danger',
            'message': 'Your thread is missing required fields.'
        },
        'profanity-filter': {
            'class': 'alert-danger',
            'message': 'Your thread contains profanity. Please remove any vulgar or offensive words and try again.'
        },
        'fail': {
            'class': 'alert-danger',
            'message': 'Failed to create your thread. Try again later.'
        }
    }, function (element) {
        return {
            'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
            'thread_title': element.find('input[name="thread_title"]').val(),
            'thread_content': tinymce.get('thread-textarea').getContent()
        }
    }, function (response, json) {
        if (response === 'success') {
            window.location.href = '/forums/t/v/' + json['thread_id'];
        }
    });
}

function setupThreadViewForms() {
    tinymce.init({
        selector: '#reply-textarea',
        code_dialog_height: 200,
        plugins: 'bbcode code',
        toolbar: 'undo redo | bold italic underline | code',
        content_css: [
            '//fonts.googleapis.com/css?family=Lato:300,300i,400,400i',
            '//www.tinymce.com/css/codepen.min.css'
        ],
        statusbar: false
    });

    new AjaxForm('/forums/t/r/' + getPageAttribute('thread_id') + '/', 'POST', 'new-reply-form', 'new-reply-response', {}, {
        'success': {
            'class': 'alert-success',
            'message': 'You created a new reply.'
        },
        'denied': {
            'class': 'alert-danger',
            'message': 'You cannot post a reply right now. This may be the result of a banned account.'
        },
        'missing-fields': {
            'class': 'alert-danger',
            'message': 'Your reply is missing required fields.'
        },
        'profanity-filter': {
            'class': 'alert-danger',
            'message': 'Your reply contains profanity. Please remove any vulgar or offensive words and try again.'
        },
        'missing-record': {
            'class': 'alert-danger',
            'message': 'This thread has been deleted.'
        },
        'thread-locked': {
            'class': 'alert-danger',
            'message': 'This thread is locked.'
        },
        'fail': {
            'class': 'alert-danger',
            'message': 'Failed to create your reply. Try again later.'
        }
    }, function (element) {
        return {
            'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
            'reply_content': tinymce.get('reply-textarea').getContent()
        }
    }, function (response, json) {
        if (response === 'success') {
            window.location.href = '/forums/t/v/' + getPageAttribute('thread_id');
        }
    });

    new AjaxButton('/forums/t/sticky/', 'POST', 'sticky-btn', 'thread-options-response', {
        'success': {
            'class': 'alert-success',
            'message': 'You have stickied this thread.'
        },
        'missing-record': {
            'class': 'alert-danger',
            'message': 'This thread has been deleted.'
        },
        'unauthorized': {
            'class': 'alert-danger',
            'message': 'You do not have permission to sticky this thread.'
        }
    }, function (element) {
        return {
            'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
            'thread_id': getPageAttribute('thread_id')
        }
    }, function (response, json) {
    });

    new AjaxButton('/forums/t/announce/', 'POST', 'announce-btn', 'thread-options-response', {
        'success': {
            'class': 'alert-success',
            'message': 'You have made this thread an announcement.'
        },
        'missing-record': {
            'class': 'alert-danger',
            'message': 'This thread has been deleted.'
        },
        'unauthorized': {
            'class': 'alert-danger',
            'message': 'You do not have permission to make this thread an announcement.'
        }
    }, function (element) {
        return {
            'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
            'thread_id': getPageAttribute('thread_id')
        }
    }, function (response, json) {
    });

    new AjaxButton('/forums/t/unlock/', 'POST', 'unlock-btn', 'thread-options-response', {
        'success': {
            'class': 'alert-success',
            'message': 'You have unlocked this thread.'
        },
        'missing-record': {
            'class': 'alert-danger',
            'message': 'This thread has been deleted.'
        },
        'unauthorized': {
            'class': 'alert-danger',
            'message': 'You do not have permission to unlock this thread.'
        }
    }, function (element) {
        return {
            'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
            'thread_id': getPageAttribute('thread_id')
        }
    }, function (response, json) {
    });

    new AjaxButton('/forums/t/lock/', 'POST', 'lock-btn', 'thread-options-response', {
        'success': {
            'class': 'alert-success',
            'message': 'You have locked this thread.'
        },
        'missing-record': {
            'class': 'alert-danger',
            'message': 'This thread has been deleted.'
        },
        'unauthorized': {
            'class': 'alert-danger',
            'message': 'You do not have permission to lock this thread.'
        }
    }, function (element) {
        return {
            'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
            'thread_id': getPageAttribute('thread_id')
        }
    }, function (response, json) {
    });

    new AjaxButton('/forums/p/delete/', 'POST', 'delete-btn', 'thread-options-response', {
        'success': {
            'class': 'alert-success',
            'message': 'You have deleted this post.'
        },
        'missing-record': {
            'class': 'alert-danger',
            'message': 'This thread has been deleted.'
        },
        'unauthorized': {
            'class': 'alert-danger',
            'message': 'You do not have permission to lock this thread.'
        }
    }, function (element) {
        return {
            'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
            'post_data': element.attr('data-post')
        }
    }, function (response, json) {
    });
}

function setupApplicationOptionForms() {
    new AjaxButton('/applications/action/', 'POST', 'action-btn', 'action-response', {
        'success': {
            'class': 'alert-success',
            'message': 'You have completed this action.'
        },
        'fail': {
            'class': 'alert-danger',
            'message': 'Failed to complete this action.'
        }
    }, function (element) {
        return {
            'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
            'application_id': getPageAttribute('application_id'),
            'action': element.attr('data-action')
        }
    }, function (response, json) {
        if (response === 'success') {
            window.location.href = '/applications/v/' + getPageAttribute('application_id');
        }
    });
}

function setupApplicationCreationForm() {
    new AjaxForm('/applications/new/', 'POST', 'new-app-form', 'new-app-response', {
        'success': {
            'class': 'alert-success',
            'message': 'You have created a new application.'
        },
        'missing-fields': {
            'class': 'alert-danger',
            'message': 'Your application is missing required fields.'
        },
        'profanity-filter': {
            'class': 'alert-danger',
            'message': 'Your application contains profanity. Remove any vulgar of offensive words and try again.'
        },
        'limited': {
            'class': 'alert-danger',
            'message': 'You already have an open staff application.'
        },
        'fail': {
            'class': 'alert-danger',
            'message': 'Failed to create your application. Try again later.'
        }
    }, {
        rules: {
            first_name: {
                required: true,
                minlength: 1,
                maxlength: 40
            },
            telegram: {
                required: true,
                minlength: 1,
                maxlength: 48
            },
            previous_experience: {
                required: true,
                minlength: 20,
                maxlength: 4096
            },
            about_yourself: {
                required: true,
                minlength: 20,
                maxlength: 4096
            },
            cheating_opinion: {
                required: true,
                minlength: 20,
                maxlength: 4096
            },
            region: {
                required: true
            },
            activity: {
                required: true
            },
            languages: {
                required: true
            }
        }
    }, function (element) {
        return {
            'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
            'first_name': element.find('input[name="first_name"]').val(),
            'telegram': element.find('input[name="telegram"]').val(),
            'previous_experience': element.find('textarea[name="previous_experience"]').val(),
            'about_yourself': element.find('textarea[name="about_yourself"]').val(),
            'cheating_opinion': element.find('textarea[name="cheating_opinion"]').val(),
            'region': element.find('select[name="region"]').val(),
            'activity': element.find('select[name="activity"]').val(),
            'languages': element.find('input[name="languages"]').val()
        }
    }, function (response, json) {

    });
}

function parseAndRenderPosts() {
    var parser = new UBB({
        defaultColor: '#333333',
        linkDefaultColor: '#0088cc'
    });

    var elements = $(".parse-content");

    elements.each(function (i) {
        var element = elements[i];
        var raw = element.innerHTML;

        raw = raw.replace(/(&amp;bull;)/ig, "•");
        raw = raw.replace(/(&lt;)/ig, "<");
        raw = raw.replace(/(&gt;)/ig, ">");
        raw = raw.replace(/(&nbsp;)/ig, " ");
        raw = raw.replace(/(<([^>]+)>)/ig, "");

        element.innerHTML = parser.UBBtoHTML(raw);
    });
}