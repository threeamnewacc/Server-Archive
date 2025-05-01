"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ExperimentalUpdatesSetting = void 0;
const React = require("react");
const settings_1 = require("../../settings");
class ExperimentalUpdatesSetting extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            enabled: settings_1.getExperimentalEnabled(),
            branch: settings_1.getExperimentalBranch()
        };
        this.branchInputRef = React.createRef();
    }
    buttonClicked(enabled) {
        this.setState({ enabled: enabled });
        settings_1.setExperimentalEnabled(enabled);
    }
    saveBranch() {
        // don't let them save text already in there if disabled
        if (!this.state.enabled)
            return;
        const branchElement = this.branchInputRef.current;
        const branch = branchElement.value;
        branchElement.value = null; // reset to empty
        // alphanumeric + _ + - between 2 and 32 chars long
        // relatively arbitrary. just to weed out the spaces, punctuation, etc
        if (branch.match(/^[a-zA-Z0-9_-]{2,32}$/)) {
            this.setState({ branch: branch });
            settings_1.setExperimentalBranch(branch);
        }
    }
    render() {
        let warning;
        if (this.state.enabled) {
            warning = React.createElement("div", { id: "experimental-desc" },
                React.createElement("i", { className: "fas fa-2x fa-exclamation-triangle mr-1" }),
                React.createElement("p", null, "Experimental branches of the client will contain bugs. Be warned."));
        }
        else {
            warning = React.createElement("div", { id: "stable-desc" },
                React.createElement("i", { className: "fas fa-2x fa-times-circle mr-1" }),
                React.createElement("p", null, "You must turn on Experimental mode to input custom branch names."));
        }
        return (React.createElement("div", { id: "branch-settings", className: "setting-category" },
            React.createElement("h1", { className: "lunar-text" },
                React.createElement("i", { className: "fas fa-flask mr-1" }),
                "Experimental Updates"),
            React.createElement("h5", null, "Do you want new, potentially buggy features"),
            React.createElement("div", null,
                React.createElement("button", { type: "button", role: "group", className: "btn lunar-text mb-2 " + (this.state.enabled ? '' : 'selected-setting'), onClick: () => this.buttonClicked(false) }, "Stable Branch"),
                React.createElement("button", { type: "button", role: "group", className: "btn lunar-text mb-3 " + (this.state.enabled ? 'selected-setting' : ''), onClick: () => this.buttonClicked(true) }, "Experimental Branch"),
                React.createElement("div", { className: "desc" }, warning),
                React.createElement("div", { id: "branchSection", style: { opacity: this.state.enabled ? 1 : 0.5 } },
                    React.createElement("div", { className: "mx-4" },
                        React.createElement("h1", null, "Experimental Branch Name"),
                        React.createElement("div", { className: "input-group" },
                            React.createElement("div", { className: "input-group-prepend" },
                                React.createElement("span", { className: "input-group-text pl-3" },
                                    React.createElement("i", { className: "fas fa-code-branch" }))),
                            React.createElement("input", { type: "text", id: "branchNameInput", placeholder: "Enter branch name...", ref: this.branchInputRef, disabled: !this.state.enabled }),
                            React.createElement("div", { className: "input-group-append" },
                                React.createElement("span", { className: "input-group-text" },
                                    React.createElement("i", { className: "fas fa-save", onClick: () => this.saveBranch() }))))),
                    React.createElement("h5", null,
                        "Your current branch is set to ",
                        React.createElement("span", null, this.state.branch))))));
    }
}
exports.ExperimentalUpdatesSetting = ExperimentalUpdatesSetting;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiRXhwZXJpbWVudGFsVXBkYXRlc1NldHRpbmcuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9zcmMvanMvY29tcG9uZW50cy9zZXR0aW5ncy9FeHBlcmltZW50YWxVcGRhdGVzU2V0dGluZy50c3giXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsK0JBQStCO0FBQy9CLDZDQUE4SDtBQU85SCxNQUFhLDBCQUEyQixTQUFRLEtBQUssQ0FBQyxTQUFvQjtJQUl0RSxZQUFZLEtBQVM7UUFDakIsS0FBSyxDQUFDLEtBQUssQ0FBQyxDQUFDO1FBQ2IsSUFBSSxDQUFDLEtBQUssR0FBRztZQUNULE9BQU8sRUFBRSxpQ0FBc0IsRUFBRTtZQUNqQyxNQUFNLEVBQUUsZ0NBQXFCLEVBQUU7U0FDbEMsQ0FBQztRQUVGLElBQUksQ0FBQyxjQUFjLEdBQUcsS0FBSyxDQUFDLFNBQVMsRUFBRSxDQUFDO0lBQzVDLENBQUM7SUFFRCxhQUFhLENBQUMsT0FBZ0I7UUFDMUIsSUFBSSxDQUFDLFFBQVEsQ0FBQyxFQUFFLE9BQU8sRUFBRSxPQUFPLEVBQUUsQ0FBQyxDQUFDO1FBQ3BDLGlDQUFzQixDQUFDLE9BQU8sQ0FBQyxDQUFDO0lBQ3BDLENBQUM7SUFFRCxVQUFVO1FBQ04sd0RBQXdEO1FBQ3hELElBQUksQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLE9BQU87WUFBRSxPQUFPO1FBRWhDLE1BQU0sYUFBYSxHQUFHLElBQUksQ0FBQyxjQUFjLENBQUMsT0FBTyxDQUFDO1FBQ2xELE1BQU0sTUFBTSxHQUFHLGFBQWEsQ0FBQyxLQUFLLENBQUM7UUFFbkMsYUFBYSxDQUFDLEtBQUssR0FBRyxJQUFJLENBQUMsQ0FBQyxpQkFBaUI7UUFFN0MsbURBQW1EO1FBQ25ELHNFQUFzRTtRQUN0RSxJQUFJLE1BQU0sQ0FBQyxLQUFLLENBQUMsdUJBQXVCLENBQUMsRUFBRTtZQUN2QyxJQUFJLENBQUMsUUFBUSxDQUFDLEVBQUUsTUFBTSxFQUFFLE1BQU0s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