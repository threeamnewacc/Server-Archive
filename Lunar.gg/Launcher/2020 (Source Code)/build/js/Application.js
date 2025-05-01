"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const React = require("react");
const ReactDom = require("react-dom");
const electron_1 = require("electron");
const constants_1 = require("./constants");
const apiCalls_1 = require("./apiCalls");
const Footer_1 = require("./components/footer/Footer");
const NavigationBar_1 = require("./components/NavigationBar");
const launch_1 = require("./launch/launch");
const settings_1 = require("./settings");
const HomePage_1 = require("./components/home/HomePage");
const ServersPage_1 = require("./components/servers/ServersPage");
const LaunchButton_1 = require("./components/LaunchButton");
const VersionSelectOverlay_1 = require("./components/VersionSelectOverlay");
const SettingsPage_1 = require("./components/settings/SettingsPage");
const AboutPage_1 = require("./components/about/AboutPage");
const launcherProfiles_1 = require("./launcherProfiles");
const path_1 = require("path");
const os_1 = require("os");
const log = require("electron-log");
// change path to be in .lunarclient folder
log.transports.file.file = path_1.join(os_1.homedir(), '.lunarclient', 'logs', 'launcher', log.transports.file.fileName);
class Application extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            activeTab: 'Home',
            versionSelectOverlay: false,
            launchButtonText: '',
            launchButtonSubtext: '',
            launchButtonState: 'ready'
        };
        // create touch bar elements
        this.touchBarLaunchButton = new electron_1.remote.TouchBar.TouchBarButton({
            label: 'Loading...',
            click: () => this.launchButtonOnClick(null)
        });
        const touchBar = new electron_1.remote.TouchBar({
            items: [
                new electron_1.remote.TouchBar.TouchBarSpacer({ size: 'flexible' }),
                this.touchBarLaunchButton,
                new electron_1.remote.TouchBar.TouchBarSpacer({ size: 'flexible' })
            ]
        });
        electron_1.remote.getCurrentWindow().setTouchBar(touchBar);
    }
    async launchButtonOnClick(autoJoin) {
        // only try to launch if we're ready
        if (this.state.launchButtonState !== 'ready') {
            return;
        }
        // utility functions to pass into launchClient
        const launchProgress = (phase, subtext) => {
            this.setState({
                launchButtonText: phase.toUpperCase(),
                launchButtonSubtext: subtext.toUpperCase(),
                launchButtonState: 'working'
            });
        };
        const versionId = settings_1.getSelectedVersion();
        const version = this.props.metadata.versions.find(x => x.id === versionId);
        const launchType = settings_1.isAnticheatEnabled(version.id) ? apiCalls_1.LaunchType.ALPHA_ANTI_LEAK : apiCalls_1.LaunchType.OFFLINE;
        const branch = settings_1.getExperimentalEnabled() ? settings_1.getExperimentalBranch() : 'master';
        try {
            await launch_1.launchClient(autoJoin, launchType, version, branch, {
                progress: launchProgress,
                success: () => {
                    launchProgress('Launched', 'Game is running');
                    switch (settings_1.getAfterLaunchAction()) {
                        case 'HIDE':
                            electron_1.remote.getCurrentWindow().hide();
                            break;
                        case 'CLOSE':
                            electron_1.remote.getCurrentWindow().close();
                            break;
                        case 'KEEP_OPEN':
                            // do nothing
                            break;
                    }
                },
                log: message => log.info('[OUTPUT] ' + message),
                exit: () => {
                    this.setState({ launchButtonText: '', launchButtonSubtext: '', launchButtonState: 'ready' });
                    electron_1.remote.getCurrentWindow().show();
                }
            });
        }
        catch (err) {
            let short = 'Unknown error';
            let description = 'An unknown error has occurred: ' + err;
            if (err.hasOwnProperty('short') && err.hasOwnProperty('description')) {
                // parse our LaunchError throwable
                short = err.short;
                description = err.description;
            }
            else {
                // print stack for other errors
                console.log(err.stack);
            }
            // set error state
            this.setState({
                launchButtonText: 'ERROR',
                launchButtonSubtext: short.toUpperCase(),
                launchButtonState: 'error'
            });
            // show warning box
            electron_1.remote.dialog.showMessageBox({
                type: 'error',
                title: 'Failed to launch Lunar Client',
                message: short,
                detail: description,
                noLink: true,
                buttons: ['Copy Info', 'OK']
            }).then(res => {
                // copy debug info
                if (res.response === 0) {
                    const lines = [
                        'Lunar Client Launch Error',
                        'Short: ' + short,
                        'Description: ' + description,
                        'Launcher Version: ' + constants_1.VERSION,
                        'OS: ' + process.platform + ' ' + process.arch,
                        'Game Version: ' + version.id + '/' + branch,
                        'Launch Type: ' + launchType
                    ];
                    electron_1.clipboard.writeText(lines.join('\n'));
                }
                // reset to ready once they exit
                this.setState({ launchButtonText: '', launchButtonSubtext: '', launchButtonState: 'ready' });
            });
        }
    }
    ;
    render() {
        const tabs = [
            { name: 'Home', onClick: () => this.setState({ activeTab: 'Home' }) },
            { name: 'Servers', onClick: () => this.setState({ activeTab: 'Servers' }) },
            { name: 'Settings', onClick: () => this.setState({ activeTab: 'Settings' }) },
            { name: 'About', onClick: () => this.setState({ activeTab: 'About' }) },
            { name: 'Store', onClick: () => electron_1.shell.openExternal(constants_1.STORE_URL) }
        ];
        let activeTab;
        switch (this.state.activeTab) {
            case 'Home':
                activeTab = React.createElement(HomePage_1.HomePage, { servers: this.props.metadata.servers, blogPosts: this.props.metadata.blogPosts, serversOnClick: s => this.launchButtonOnClick(s) });
                break;
            case 'Servers':
                activeTab = React.createElement(ServersPage_1.ServersPage, { servers: this.props.metadata.servers, serversOnClick: s => this.launchButtonOnClick(s) });
                break;
            case 'Settings':
                activeTab = React.createElement(SettingsPage_1.SettingsPage, null);
                break;
            case 'About':
                activeTab = React.createElement(AboutPage_1.AboutPage, null);
                break;
        }
        let launchButtonText = this.state.launchButtonText;
        let launchButtonSubtext = this.state.launchButtonSubtext;
        if (this.state.launchButtonState === 'ready') {
            let selectedVersion = settings_1.getSelectedVersion();
            let anticheatEnabled = settings_1.isAnticheatEnabled(selectedVersion);
            launchButtonText = 'LAUNCH ' + selectedVersion;
            launchButtonSubtext = React.createElement(React.Fragment, null,
                React.createElement("i", { className: "mr-1 fas fa-" + (anticheatEnabled ? "user-shield" : "ban") }),
                "ANTICHEAT ",
                anticheatEnabled ? "ENABLED" : "DISABLED");
        }
        return (React.createElement(React.Fragment, null,
            React.createElement(NavigationBar_1.NavigationBar, { tabs: tabs, activeTab: this.state.activeTab, launcherProfiles: this.props.launcherProfiles }),
            this.state.versionSelectOverlay ?
                React.createElement(VersionSelectOverlay_1.VersionSelectOverlay, { versions: this.props.metadata.versions, onExit: () => this.setState({ versionSelectOverlay: false }) })
                : null,
            React.createElement("div", { id: "content" },
                React.createElement(LaunchButton_1.LaunchButton, { text: launchButtonText, subtext: launchButtonSubtext, state: this.state.launchButtonState, height: this.state.activeTab === 'Home' ? 34 : 18, touchBarButton: this.touchBarLaunchButton, dropdownOnClick: () => this.setState({ versionSelectOverlay: true }), buttonOnClick: () => this.launchButtonOnClick(null) }),
                activeTab),
            React.createElement(Footer_1.Footer, null)));
    }
}
Promise.all([apiCalls_1.requestMetadata(), launcherProfiles_1.loadLauncherProfiles()]).then(res => {
    ReactDom.render(React.createElement(Application, { metadata: res[0], launcherProfiles: res[1] }), document.getElementById('application'));
    electron_1.ipcRenderer.send('ready');
    log.info('Launcher ID: ' + constants_1.HWID + '. Used for analytics / log tracing.');
});
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiQXBwbGljYXRpb24uanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvanMvQXBwbGljYXRpb24udHN4Il0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7O0FBQUEsK0JBQStCO0FBQy9CLHNDQUFzQztBQUN0Qyx1Q0FBaUU7QUFDakUsMkNBQXFEO0FBQ3JELHlDQUFpRjtBQUNqRix1REFBa0Q7QUFDbEQsOERBQXlEO0FBQ3pELDRDQUEwRDtBQUMxRCx5Q0FNb0I7QUFDcEIseURBQW9EO0FBQ3BELGtFQUE2RDtBQUM3RCw0REFBdUQ7QUFDdkQsNEVBQXVFO0FBQ3ZFLHFFQUFnRTtBQUNoRSw0REFBdUQ7QUFDdkQseURBQTBFO0FBQzFFLCtCQUEwQjtBQUMxQiwyQkFBMkI7QUFDM0Isb0NBQXFDO0FBRXJDLDJDQUEyQztBQUMzQyxHQUFHLENBQUMsVUFBVSxDQUFDLElBQUksQ0FBQyxJQUFJLEdBQUcsV0FBSSxDQUFDLFlBQU8sRUFBRSxFQUFFLGNBQWMsRUFBRSxNQUFNLEVBQUUsVUFBVSxFQUFFLEdBQUcsQ0FBQyxVQUFVLENBQUMsSUFBSSxDQUFDLFFBQVEsQ0FBQyxDQUFDO0FBZ0I3RyxNQUFNLFdBQVksU0FBUSxLQUFLLENBQUMsU0FBdUI7SUFJbkQsWUFBWSxLQUFZO1FBQ3BCLEtBQUssQ0FBQyxLQUFLLENBQUMsQ0FBQztRQUNiLElBQUksQ0FBQyxLQUFLLEdBQUc7WUFDVCxTQUFTLEVBQUUsTUFBTTtZQUNqQixvQkFBb0IsRUFBRSxLQUFLO1lBRTNCLGdCQUFnQixFQUFFLEVBQUU7WUFDcEIsbUJBQW1CLEVBQUUsRUFBRTtZQUN2QixpQkFBaUIsRUFBRSxPQUFPO1NBQzdCLENBQUM7UUFFRiw0QkFBNEI7UUFDNUIsSUFBSSxDQUFDLG9CQUFvQixHQUFHLElBQUksaUJBQU0sQ0FBQyxRQUFRLENBQUMsY0FBYyxDQUFDO1lBQzNELEtBQUssRUFBRSxZQUFZO1lBQ25CLEtBQUssRUFBRSxHQUFHLEVBQUUsQ0FBQyxJQUFJLENBQUMsbUJB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