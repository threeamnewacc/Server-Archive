var loaded = {
    'staff': false,
    'lmlogs': false
};

var staffTab = null;
var staffContent = null;
var lordMemeLogsTab = null;
var lordMemeLogsContent = null;

function loadDynamicTab(type, tab, url) {
    if (loaded[type] === false) {
        loaded[type] = true;

        $.get(url, function (response) {
            tab.html(response);
            tab.removeClass('loading');

            refreshPlugins();
        });
    }
}

$(document).ready(function () {
    jQuery(document).ready(
        function () {
            staffTab = $("#staff-tab");
            staffContent = $("#staff-content");
            lordMemeLogsTab = $("#lmlogs-tab");
            lordMemeLogsContent = $("#lmlogs-content");

            staffTab.click(
                function(event) {
                    loadDynamicTab('staff', staffContent, '/partial/player/staff/' + getPageAttribute('player_uuid'));
                }
            );

            lordMemeLogsTab.click(
                function (event) {
                    loadDynamicTab('lmlogs', lordMemeLogsContent, '/partial/player/lmlogs/' + getPageAttribute('player_uuid'));
                }
            );
        }
    );
});