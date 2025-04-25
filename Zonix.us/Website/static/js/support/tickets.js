function setupTicketViewOptions() {
    var optionsResponse = $("#options-response");

    optionsResponse.hide();

    $(".close-btn").click(
        function (event) {
            optionsResponse.html('');
            optionsResponse.hide();

            $.post({
                method: "POST",
                url: "/support/tickets/c/" + getPageAttribute('ticket_id'),
                data: {
                    'csrfmiddlewaretoken': getPageAttribute('csrf_token')
                },
                dataType: "json"
            }).done(function (json) {
                var response = json['response'];

                if (response === 'success') {
                    window.location.href = '/support/tickets/v/' + getPageAttribute('ticket_id');
                }
                else {
                    var responseMessage = 'Failed to close this ticket.';

                    if (response === 'missing-record') {
                        responseMessage = 'That ticket could not be found. Maybe it has been deleted?';
                    }
                    else if (response === 'missing-fields') {
                        responseMessage = 'Malformed request.';
                    }
                    else if (response === 'unauthorized') {
                        responseMessage = 'You are not authorized to close that ticket.';
                    }
                    else if (response === 'ticket-closed') {
                        responseMessage = 'That ticket has already been closed.';
                    }

                    optionsResponse.html(responseMessage);
                    optionsResponse.show();
                }
            }).fail(function () {
                optionsResponse.html('Failed to complete that action.');
                optionsResponse.show();
            });
        }
    );

    $(".open-btn").click(
        function (event) {
            optionsResponse.html('');
            optionsResponse.hide();

            $.post({
                method: "POST",
                url: "/support/tickets/o/" + getPageAttribute('ticket_id'),
                data: {
                    'csrfmiddlewaretoken': getPageAttribute('csrf_token')
                },
                dataType: "json"
            }).done(function (json) {
                var response = json['response'];

                if (response === 'success') {
                    window.location.href = '/support/tickets/v/' + getPageAttribute('ticket_id');
                }
                else {
                    var responseMessage = 'Failed to open this ticket.';

                    if (response === 'missing-record') {
                        responseMessage = 'That ticket does not exist.';
                    }
                    else if (response === 'missing-fields') {
                        responseMessage = 'Malformed request.';
                    }
                    else if (response === 'unauthorized') {
                        responseMessage = 'You are not authorized to open that ticket.';
                    }

                    optionsResponse.html(responseMessage);
                    optionsResponse.show();
                }
            }).fail(function () {
                optionsResponse.html('Failed to complete that action.');
                optionsResponse.show();
            });
        }
    );

    $(".reassign-btn").click(
        function (event) {
            optionsResponse.html('');
            optionsResponse.hide();

            $.post({
                method: "POST",
                url: "/support/tickets/ra/" + getPageAttribute('ticket_id'),
                data: {
                    'csrfmiddlewaretoken': getPageAttribute('csrf_token')
                },
                dataType: "json"
            }).done(function (json) {
                var response = json['response'];

                if (response === 'success') {
                    window.location.href = '/support/tickets/v/' + getPageAttribute('ticket_id');
                }
                else {
                    var responseMessage = 'Failed to re-assign this ticket.';

                    if (response === 'missing-record') {
                        responseMessage = 'That ticket could not be found. Maybe it has been deleted?';
                    }
                    else if (response === 'missing-fields') {
                        responseMessage = 'Malformed request.';
                    }
                    else if (response === 'unauthorized') {
                        responseMessage = 'You are not authorized to re-assign that ticket.';
                    }

                    optionsResponse.html(responseMessage);
                    optionsResponse.show();
                }
            }).fail(function () {
                optionsResponse.html('Failed to complete that action.');
                optionsResponse.show();
            });
        }
    );
}

function setupTicketReply() {
    var replyResponse = $('#reply-response');
    var replyForm = $('#reply-form');
    var replyValidator = replyForm.validate({
        rules: {
            reply_content: {
                required: true,
                minlength: 8,
                maxlength: 2048
            }
        }
    });

    replyResponse.hide();
    replyResponse.html('');

    replyForm.submit(function (event) {
        event.preventDefault();

        if (!replyForm.valid()) {
            replyValidator.showErrors();
            return;
        }

        var current = new Date().getTime();
        var last = getPageAttribute('last_submit');

        if (last !== undefined && current < last + 5000) {
            replyResponse.show();
            replyResponse.addClass('alert-danger');
            replyResponse.html('Please wait before trying to submit the form again.');
            return;
        }

        setPageAttribute('last_submit', current);

        replyResponse.hide();
        replyResponse.html('');
        replyResponse.removeClass('alert-danger');

        $.post({
            method: "POST",
            url: "/support/tickets/r/",
            data: {
                'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
                'ticket_id': getPageAttribute('ticket_id'),
                'reply_content': replyForm.find('textarea[name="reply_content"]').val()
            },
            dataType: "json"
        }).done(function (json) {
            var response = json['response'];

            if (response === 'success') {
                window.location.href = '/support/tickets/v/' + getPageAttribute('ticket_id');
            }
            else {
                var responseMessage = 'Failed to create your ticket reply.';

                if (response === 'missing-fields') {
                    responseMessage = 'You are missing fields that are required.';
                }
                else if (response === 'missing-record') {
                    responseMessage = 'That ticket does not exist.';
                }
                else if (response === 'profanity-filter') {
                    responseMessage = 'Your ticket reply contains profanity. Please remove any vulgar or offensive words and try again.';
                }
                else if (response === 'ticket-closed') {
                    responseMessage = 'That ticket is closed.';
                }

                replyResponse.show();
                replyResponse.addClass('alert-danger');
                replyResponse.html(responseMessage);
            }
        }).fail(function () {
            replyResponse.show();
            replyResponse.addClass('alert-danger');
            replyResponse.html('Failed to create your ticket reply.');
        });
    });
}

function setupTicketNew() {
    var newTicketResponse = $('#new-ticket-response');
    var newTicketForm = $('#new-ticket-form');
    var newTicketValidator = newTicketForm.validate({
        rules: {
            ticket_subject: {
                required: true,
                minlength: 4,
                maxlength: 40
            },
            ticket_content: {
                required: true,
                minlength: 30,
                maxlength: 2000
            }
        }
    });

    newTicketResponse.hide();
    newTicketResponse.html('');

    newTicketForm.submit(function (event) {
        event.preventDefault();

        if (!newTicketForm.valid()) {
            newTicketValidator.showErrors();
            return;
        }

        var current = new Date().getTime();
        var last = getPageAttribute('last_submit');

        if (last !== undefined && current < last + 5000) {
            newTicketResponse.show();
            newTicketResponse.addClass('alert-danger');
            newTicketResponse.html('Please wait before trying to submit the form again.');
            return;
        }

        setPageAttribute('last_submit', current);

        newTicketResponse.hide();
        newTicketResponse.html('');
        newTicketResponse.removeClass('alert-danger');

        $.post({
            method: "POST",
            url: "/support/tickets/new/",
            data: {
                'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
                'ticket_subject': newTicketForm.find('input[name="ticket_subject"]').val(),
                'ticket_content': newTicketForm.find('textarea[name="ticket_content"]').val(),
                'ticket_type': newTicketForm.find('select[name="ticket_type"]').val()
            },
            dataType: "json"
        }).done(function (json) {
            var response = json['response'];

            if (response === 'success') {
                window.location.href = '/support/tickets/v/' + json['ticket_id'];
            }
            else {
                var responseMessage = 'Failed to create your ticket.';

                if (response === 'missing-fields') {
                    responseMessage = 'You are missing fields that are required.';
                }
                else if (response === 'profanity-filter') {
                    responseMessage = 'Your ticket contains profanity. Please remove any vulgar or offensive words and try again.';
                }
                else if (response === 'limited') {
                    responseMessage = 'You have too many active tickets to create another ticket.';
                }

                newTicketResponse.css('display', 'block');
                newTicketResponse.addClass('alert-danger');
                newTicketResponse.html(responseMessage);
            }
        }).fail(function () {
            newTicketResponse.css('display', 'block');
            newTicketResponse.addClass('alert-danger');
            newTicketResponse.html('Failed to create your ticket.');
        });
    });
}