"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.LaunchDirectorySetting = void 0;
const React = require("react");
const settings_1 = require("../../settings");
const electron_1 = require("electron");
const os_1 = require("os");
class LaunchDirectorySetting extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            directory: settings_1.getLaunchDirectory()
        };
    }
    openDialog() {
        const result = electron_1.remote.dialog.showOpenDialogSync({
            title: 'Select new Minecraft directory',
            defaultPath: settings_1.getLaunchDirectory(),
            properties: ['openDirectory', 'createDirectory', 'showHiddenFiles']
        });
        // undefined means they just closed the window w/o hitting select
        if (result !== undefined) {
            const directory = result[0];
            this.setState({ directory: directory });
            settings_1.setLaunchDirectory(directory);
        }
    }
    render() {
        const username = os_1.userInfo().username;
        const replaceWith = '*'.repeat(username.length);
        const censoredDirectory = this.state.directory.replace(username, replaceWith);
        return (React.createElement("div", { id: "launch-directory", className: "setting-category" },
            React.createElement("h1", { className: "lunar-text" },
                React.createElement("i", { className: "far fa-folder-open mr-1" }),
                "Launch Directory"),
            React.createElement("h5", null, "Select which directory to launch Minecraft from"),
            React.createElement("button", { type: "button", className: "btn lunar-text aboutButton mb-3", onClick: () => this.openDialog() },
                React.createElement("i", { className: "fas fa-gamepad mr-1" }),
                "Change Directory"),
            React.createElement("h4", null, "Your current directory is set to:"),
            React.createElement("h3", { id: "current-directory" }, censoredDirectory)));
    }
}
exports.LaunchDirectorySetting = LaunchDirectorySetting;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiTGF1bmNoRGlyZWN0b3J5U2V0dGluZy5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3NyYy9qcy9jb21wb25lbnRzL3NldHRpbmdzL0xhdW5jaERpcmVjdG9yeVNldHRpbmcudHN4Il0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7OztBQUFBLCtCQUErQjtBQUMvQiw2Q0FBd0U7QUFDeEUsdUNBQWtDO0FBQ2xDLDJCQUE4QjtBQU05QixNQUFhLHNCQUF1QixTQUFRLEtBQUssQ0FBQyxTQUFvQjtJQUVsRSxZQUFZLEtBQVM7UUFDakIsS0FBSyxDQUFDLEtBQUssQ0FBQyxDQUFDO1FBQ2IsSUFBSSxDQUFDLEtBQUssR0FBRztZQUNULFNBQVMsRUFBRSw2QkFBa0IsRUFBRTtTQUNsQyxDQUFDO0lBQ04sQ0FBQztJQUVELFVBQVU7UUFDTixNQUFNLE1BQU0sR0FBRyxpQkFBTSxDQUFDLE1BQU0sQ0FBQyxrQkFBa0IsQ0FBQztZQUM1QyxLQUFLLEVBQUUsZ0NBQWdDO1lBQ3ZDLFdBQVcsRUFBRSw2QkFBa0IsRUFBRTtZQUNqQyxVQUFVLEVBQUUsQ0FBQyxlQUFlLEVBQUUsaUJBQWlCLEVBQUUsaUJBQWlCLENBQUM7U0FDdEUsQ0FBQyxDQUFDO1FBRUgsaUVBQWlFO1FBQ2pFLElBQUksTUFBTSxLQUFLLFNBQVMsRUFBRTtZQUN0QixNQUFNLFNBQVMsR0FBRyxNQUFNLENBQUMsQ0FBQyxDQUFDLENBQUM7WUFFNUIsSUFBSSxDQUFDLFFBQVEsQ0FBQyxFQUFFLFNBQVMsRUFBRSxTQUFTLEVBQUUsQ0FBQyxDQUFDO1lBQ3hDLDZCQUFrQixDQUFDLFNBQVMsQ0FBQyxDQUFDO1NBQ2pDO0lBQ0wsQ0FBQztJQUVELE1BQU07UUFDRixNQUFNLFFBQVEsR0FBRyxhQUFRLEVBQUUsQ0FBQyxRQUFRLENBQUM7UUFDckMsTUFBTSxXQUFXLEdBQUcsR0FBRyxDQUFDLE1BQU0sQ0FBQyxRQUFRLENBQUMsTUFBTSxDQUFDLENBQUM7UUFDaEQsTUFBTSxpQkFBaUIsR0FBRyxJQUFJLENBQUMsS0FBSyxDQUFDLFNBQVMsQ0FBQyxPQUFPLENBQUMsUUFBUSxFQUFFLFdBQVcsQ0FBQyxDQUFDO1FBRTlFLE9BQU8sQ0FBQyw2QkFBSyxFQUFFLEVBQUMsa0JBQWtCLEVBQUMsU0FBUyxFQUFDLGtCQUFrQjtZQUMzRCw0QkFBSSxTQUFTLEVBQUMsWUFBWTtnQkFDdEIsMkJBQUcsU0FBUyxFQUFDLHlCQUF5QixHQUFHO21DQUV4QztZQUNMLGtGQUF3RDtZQUN4RCxnQ0FBUSxJQUFJLEVBQUMsUUFBUSxFQUFDLFNBQVMsRUFBQyxpQ0FBaUMsRUFBQyxPQUFPLEVBQUUsR0FBRyxFQUFFLENBQUMsSUFBSSxDQUFDLFVBQVUsRUFBRTtnQkFDOUYsMkJBQUcsU0FBUyxFQUFDLHFCQUFxQixHQUFHO21DQUVoQztZQUNULG9FQUEwQztZQUMxQyw0QkFBSSxFQUFFLEVBQUMsbUJBQW1CLElBQUUsaUJBQWlCLENBQU0sQ0FDakQsQ0FBQyxDQUFDO0lBQ1osQ0FBQztDQUVKO0FBN0NELHdEQTZDQyJ9