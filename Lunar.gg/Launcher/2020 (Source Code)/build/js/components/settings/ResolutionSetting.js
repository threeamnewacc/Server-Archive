"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ResolutionSetting = void 0;
const React = require("react");
const settings_1 = require("../../settings");
const ResolutionPresetMenu_1 = require("./ResolutionPresetMenu");
class ResolutionSetting extends React.Component {
    constructor(props) {
        super(props);
        const [width, height] = settings_1.getResolution();
        this.state = {
            width: width,
            height: height
        };
        this.widthRef = React.createRef();
        this.heightRef = React.createRef();
        this.selectResolution = this.selectResolution.bind(this);
        this.saveResolution = this.saveResolution.bind(this);
    }
    selectResolution(width, height) {
        // clear inputs
        this.widthRef.current.value = null;
        this.heightRef.current.value = null;
        // save settings
        this.setState({ width: width, height: height });
        settings_1.setResolution(width, height);
    }
    saveResolution() {
        // grab existing
        let width = this.state.width;
        let height = this.state.height;
        // parse from inputs
        const pendingWidth = Number.parseInt(this.widthRef.current.value);
        const pendingHeight = Number.parseInt(this.heightRef.current.value);
        // commit if valid
        if (pendingWidth > 0 && pendingWidth < 10000) {
            width = pendingWidth;
        }
        if (pendingHeight > 0 && pendingHeight < 10000) {
            height = pendingHeight;
        }
        // clear inputs
        this.widthRef.current.value = null;
        this.heightRef.current.value = null;
        // save settings
        this.setState({ width: width, height: height });
        settings_1.setResolution(width, height);
    }
    render() {
        return (React.createElement("div", { id: "resolution", className: "setting-category" },
            React.createElement("h1", { className: "lunar-text" },
                React.createElement("i", { className: "fas fa-compress mr-1" }),
                "Resolution"),
            React.createElement("h5", null, "Enter a resolution to launch your game in"),
            React.createElement("div", { id: "resolution-input-headers", className: "row no-gutters" },
                React.createElement("div", { className: "col-3" },
                    React.createElement("p", { className: "lunar-text" },
                        React.createElement("i", { className: "fas fa-text-width mr-1" }),
                        "Width")),
                React.createElement("div", { className: "col-1" }),
                React.createElement("div", { className: "col-3" },
                    React.createElement("p", { className: "lunar-text" },
                        React.createElement("i", { className: "fas fa-text-height mr-1" }),
                        "Height"))),
            React.createElement("div", { id: "resolution-input-boxes", className: "row no-gutters" },
                React.createElement("div", { className: "col-3" },
                    React.createElement("input", { id: "width-input", ref: this.widthRef, className: "form-control", type: "text", placeholder: this.state.width.toString() })),
                React.createElement("div", { className: "col-1" },
                    React.createElement("p", null, "X")),
                React.createElement("div", { className: "col-3" },
                    React.createElement("input", { id: "height-input", ref: this.heightRef, className: "form-control", type: "text", placeholder: this.state.height.toString() }))),
            React.createElement("div", { className: "mt-2" },
                React.createElement(ResolutionPresetMenu_1.ResolutionPresetMenu, { onSelect: this.selectResolution }),
                React.createElement("button", { type: "button", className: "btn lunar-text aboutButton", onClick: this.saveResolution },
                    React.createElement("i", { className: "fas fa-save mr-1" }),
                    "Save"))));
    }
}
exports.ResolutionSetting = ResolutionSetting;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiUmVzb2x1dGlvblNldHRpbmcuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9zcmMvanMvY29tcG9uZW50cy9zZXR0aW5ncy9SZXNvbHV0aW9uU2V0dGluZy50c3giXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsK0JBQStCO0FBQy9CLDZDQUE4RDtBQUM5RCxpRUFBOEQ7QUFPOUQsTUFBYSxpQkFBa0IsU0FBUSxLQUFLLENBQUMsU0FBb0I7SUFLN0QsWUFBWSxLQUFTO1FBQ2pCLEtBQUssQ0FBQyxLQUFLLENBQUMsQ0FBQztRQUNiLE1BQU0sQ0FBQyxLQUFLLEVBQUUsTUFBTSxDQUFDLEdBQUcsd0JBQWEsRUFBRSxDQUFDO1FBRXhDLElBQUksQ0FBQyxLQUFLLEdBQUc7WUFDVCxLQUFLLEVBQUUsS0FBSztZQUNaLE1BQU0sRUFBRSxNQUFNO1NBQ2pCLENBQUM7UUFFRixJQUFJLENBQUMsUUFBUSxHQUFHLEtBQUssQ0FBQyxTQUFTLEVBQUUsQ0FBQztRQUNsQyxJQUFJLENBQUMsU0FBUyxHQUFHLEtBQUssQ0FBQyxTQUFTLEVBQUUsQ0FBQztRQUVuQyxJQUFJLENBQUMsZ0JBQWdCLEdBQUcsSUFBSSxDQUFDLGdCQUFnQixDQUFDLElBQUksQ0FBQyxJQUFJLENBQUMsQ0FBQztRQUN6RCxJQUFJLENBQUMsY0FBYyxHQUFHLElBQUksQ0FBQyxjQUFjLENBQUMsSUFBSSxDQUFDLElBQUksQ0FBQyxDQUFDO0lBQ3pELENBQUM7SUFFRCxnQkFBZ0IsQ0FBQyxLQUFhLEVBQUUsTUFBYztRQUMxQyxlQUFlO1FBQ2YsSUFBSSxDQUFDLFFBQVEsQ0FBQyxPQUFPLENBQUMsS0FBSyxHQUFHLElBQUksQ0FBQztRQUNuQyxJQUFJLENBQUMsU0FBUyxDQUFDLE9BQU8sQ0FBQyxLQUFLLEdBQUcsSUFBSSxDQUFDO1FBRXBDLGdCQUFnQjtRQUNoQixJQUFJLENBQUMsUUFBUSxDQUFDLEVBQUUsS0FBSyxFQUFFLEtBQUssRUFBRSxNQUFNLEVB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