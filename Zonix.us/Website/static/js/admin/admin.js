function setupTicketManagement() {
    var aorResponse = $('#aor-response');
    var aorForm = $('#aor-form');
    var aorValidator = aorForm.validate({
        rules: {
            uuid: {
                required: true,
                minlength: 16
            }
        }
    });

    aorResponse.hide();

    aorForm.submit(
        function (event) {
            event.preventDefault();

            if (!aorValidator.valid()) {
                aorValidator.showErrors();
                return;
            }

            aorResponse.hide();
            aorResponse.removeClass('alert-danger');
            aorResponse.removeClass('alert-success');
            aorResponse.html('');

            $.post({
                method: "POST",
                url: "/admin/tickets/ticket-blacklist/",
                data: {
                    'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
                    'uuid': aorForm.find('input[name="uuid"]').val()
                },
                dataType: "json"
            }).done(function (json) {
                var response = json['response'];

                if (response === 'success') {
                    aorResponse.show();
                    aorResponse.addClass('alert-success');
                    aorResponse.html('You have added or removed <strong>' + aorForm.find('input[name="uuid"]').val() + '</strong> to or from the ticket blacklist.');
                }
                else {
                    aorResponse.show();
                    aorResponse.addClass('alert-danger');
                    aorResponse.html('Failed to complete action.');
                }
            }).fail(function () {
                aorResponse.show();
                aorResponse.addClass('alert-danger');
                aorResponse.html('Failed to complete action.');
            });
        }
    );
}