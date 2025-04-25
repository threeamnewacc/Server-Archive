var pageAttributes = {};

function getPageAttribute(key) {
    return pageAttributes[key];
}

function setPageAttribute(key, value) {
    pageAttributes[key] = value;
}

function hasAttribute(element, name) {
    return element.attr(name) !== undefined;
}

function setupNotifications() {
    var notificationCountElement = $("#notification-count");

    $(".notification-item").hover(
        function (event) {
            var element = $(this);

            if (hasAttribute(element, 'data-seen') && element.attr('data-seen') === 'false') {
                element.attr('data-seen', 'true');

                if (notificationCountElement !== null) {
                    var currentCount = notificationCountElement.html();
                    var newCount = currentCount - 1;

                    if (newCount <= 0) {
                        notificationCountElement.hide();
                    }
                    else {
                        notificationCountElement.html(currentCount - 1);
                    }
                }

                $.post({
                    method: "POST",
                    url: '/notifications/seen/',
                    data: {
                        'csrfmiddlewaretoken': getPageAttribute('csrf_token'),
                        'notification_id': $(this).attr('data-notification')
                    },
                    dataType: "json"
                });
            }
        }
    )
}

function setupSearch() {
    setPageAttribute('searched', false);

    var lookup = $("#player-search-input");
    var lookupResults = $("#lookup-results");

    $(lookup).keyup(
        function () {
            if (getPageAttribute('searched') === false) {
                lookupResults.html('<ul><li>Searching...</li></ul>');
                setPageAttribute('searched', true);
            }

            if (lookup.val().length > 0) {
                $.get('/partial/search_results/' + lookup.val(), function (html) {
                    lookupResults.html(html);
                });
            }

            lookupResults.show();
        }
    );

    $('body').on('click', function (e) {
        if ($(e.target).not(lookupResults)) {
            lookupResults.hide();
        }
    });
}

function refreshPlugins() {
    jQuery.timeago.settings.allowFuture = true;
    jQuery.timeago.settings.allowPast = true;
    jQuery("time.timeago").timeago();
}

$(document).ready(function () {
    jQuery(document).ready(
        function () {
            setupNotifications();
            setupSearch();
            refreshPlugins();
        }
    );
});