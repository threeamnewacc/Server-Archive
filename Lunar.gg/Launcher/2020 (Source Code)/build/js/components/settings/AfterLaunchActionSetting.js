"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.AfterLaunchActionSetting = void 0;
const React = require("react");
const settings_1 = require("../../settings");
class AfterLaunchActionSetting extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            action: settings_1.getAfterLaunchAction()
        };
    }
    buttonClicked(action) {
        this.setState({ action: action });
        settings_1.setAfterLaunchAction(action);
    }
    render() {
        return (React.createElement("div", { id: "on-launch-settings", className: "setting-category" },
            React.createElement("h1", { className: "lunar-text" },
                React.createElement("i", { className: "fas fa-play-circle mr-1" }),
                "After Launch"),
            React.createElement("h5", null, "Select which action your launcher should take on launch"),
            React.createElement("button", { type: "button", role: "group", className: "btn lunar-text mb-2 " + (this.state.action == 'HIDE' ? 'selected-setting' : ''), onClick: () => this.buttonClicked('HIDE') }, "Hide Launcher"),
            React.createElement("br", null),
            React.createElement("button", { type: "button", role: "group", className: "btn lunar-text mb-2 " + (this.state.action == 'KEEP_OPEN' ? 'selected-setting' : ''), onClick: () => this.buttonClicked('KEEP_OPEN') }, "Keep Launcher Open"),
            React.createElement("br", null),
            React.createElement("button", { type: "button", role: "group", className: "btn lunar-text " + (this.state.action == 'CLOSE' ? 'selected-setting' : ''), onClick: () => this.buttonClicked('CLOSE') }, "Close Launcher")));
    }
}
exports.AfterLaunchActionSetting = AfterLaunchActionSetting;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiQWZ0ZXJMYXVuY2hBY3Rpb25TZXR0aW5nLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vc3JjL2pzL2NvbXBvbmVudHMvc2V0dGluZ3MvQWZ0ZXJMYXVuY2hBY3Rpb25TZXR0aW5nLnRzeCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFBQSwrQkFBK0I7QUFDL0IsNkNBQTRFO0FBTTVFLE1BQWEsd0JBQXlCLFNBQVEsS0FBSyxDQUFDLFNBQW9CO0lBRXBFLFlBQVksS0FBUztRQUNqQixLQUFLLENBQUMsS0FBSyxDQUFDLENBQUM7UUFDYixJQUFJLENBQUMsS0FBSyxHQUFHO1lBQ1QsTUFBTSxFQUFFLCtCQUFvQixFQUFFO1NBQ2pDLENBQUM7SUFDTixDQUFDO0lBRUQsYUFBYSxDQUFDLE1BQXNDO1FBQ2hELElBQUksQ0FBQyxRQUFRLENBQUMsRUFBRSxNQUFNLEVBQUUsTUFBTSxFQUFFLENBQUMsQ0FBQztRQUNsQywrQkFBb0IsQ0FBQyxNQUFNLENBQUMsQ0FBQztJQUNqQyxDQUFDO0lBRUQsTUFBTTtRQUNGLE9BQU8sQ0FBQyw2QkFBSyxFQUFFLEVBQUMsb0JBQW9CLEVBQUMsU0FBUyxFQUFDLGtCQUFrQjtZQUM3RCw0QkFBSSxTQUFTLEVBQUMsWUFBWTtnQkFDdEIsMkJBQUcsU0FBUyxFQUFDLHlCQUF5QixHQUFHOytCQUV4QztZQUNMLDBGQUFnRTtZQUNoRSxnQ0FBUSxJQUFJLEVBQUMsUUFBUSxFQUFDLElBQUksRUFBQyxPQUFPLEVBQUMsU0FBUyxFQUFFLHNCQUFzQixHQUFHLENBQUMsSUFBSSxDQUFDLEtBQUssQ0FBQyxNQUFNLElBQUksTUFBTSxDQUFDLENBQUMsQ0FBQyxrQkFBa0IsQ0FBQyxDQUFDLENBQUMsRUFBRSxDQUFDLEVBQUUsT0FBTyxFQUFFLEdBQUcsRUFBRSxDQUFDLElBQUksQ0FBQyxhQUFhLENBQUMsTUFBTSxDQUFDLG9CQUVoSztZQUNULCtCQUFNO1lBQ04sZ0NBQVEsSUFBSSxFQUFDLFFBQVEsRUFBQyxJQUFJLEVBQUMsT0FBTyxFQUFDLFNBQVMsRUFBRSxzQkFBc0IsR0FBRyxDQUFDLElBQUksQ0FBQyxLQUFLLENBQUMsTUFBTSxJQUFJLFdBQVcsQ0FBQyxDQUFDLENBQUMsa0JBQWtCLENBQUMsQ0FBQyxDQUFDLEVBQUUsQ0FBQyxFQUFFLE9BQU8sRUFBRSxHQUFHLEVBQUUsQ0FBQyxJQUFJLENBQUMsYUFBYSxDQUFDLFdBQVcsQ0FBQyx5QkFFMUs7WUFDVCwrQkFBTTtZQUNOLGdDQUFRLElBQUksRUFBQyxRQUFRLEVBQUMsSUFBSSxFQUFDLE9BQU8sRUFBQyxTQUFTLEVBQUUsaUJBQWlCLEdBQUcsQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLE1BQU0sSUFBSSxPQUFPLENBQUMsQ0FBQyxDQUFDLGtCQUFrQixDQUFDLENBQUMsQ0FBQyxFQUFFLENBQUMsRUFBRSxPQUFPLEVBQUUsR0FBRyxFQUFFLENBQUMsSUFBSSxDQUFDLGFBQWEsQ0FBQyxPQUFPLENBQUMscUJBRTdKLENBQ1AsQ0FBQyxDQUFDO0lBQ1osQ0FBQztDQUVKO0FBbkNELDREQW1DQyJ9