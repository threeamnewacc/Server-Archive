"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ResolutionPresetMenu = void 0;
const React = require("react");
const ResolutionPresetItem_1 = require("./ResolutionPresetItem");
class ResolutionPresetMenu extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            expanded: false
        };
        this.onSelect = this.onSelect.bind(this);
        this.toggle = this.toggle.bind(this);
        this.escapeButton = this.escapeButton.bind(this);
        this.mouseClicked = this.mouseClicked.bind(this);
    }
    onSelect(width, height) {
        this.setState({ expanded: false });
        this.props.onSelect(width, height);
    }
    toggle() {
        this.setState({ expanded: !this.state.expanded });
    }
    escapeButton(event) {
        if (event.code === 'Escape') {
            this.setState({ expanded: false });
        }
    }
    mouseClicked(event) {
        const versions = document.getElementById('presetsArea');
        if (event.target instanceof Node && !versions.contains(event.target)) {
            this.setState({ expanded: false });
        }
    }
    componentDidMount() {
        document.addEventListener('keydown', this.escapeButton, false);
        document.addEventListener('click', this.mouseClicked, true);
    }
    componentWillUnmount() {
        document.removeEventListener('keydown', this.escapeButton, false);
        document.removeEventListener('click', this.mouseClicked, true);
    }
    render() {
        return (React.createElement("div", { id: "presetsArea", className: "btn-group dropup mr-2" },
            React.createElement("button", { type: "button", className: "btn lunar-text aboutButton dropdown-toggle", onClick: this.toggle },
                React.createElement("i", { className: "fas fa-desktop mr-1" }),
                "Presets"),
            React.createElement("div", { id: "presets-menu", className: "dropdown-menu" + (this.state.expanded ? " show" : "") },
                React.createElement("h1", null,
                    React.createElement("i", { className: "fas fa-desktop mr-2" }),
                    "Resolution Presets"),
                React.createElement("h5", null, "Select a preset resolution for your Minecraft window"),
                React.createElement("div", { className: "row no-gutters" },
                    React.createElement("div", { className: "col-4" },
                        React.createElement("h6", { className: "dropdown-header" }, "4:3 Ratio"),
                        React.createElement(ResolutionPresetItem_1.ResolutionPresetItem, { width: 640, height: 480, onSelect: this.onSelect }),
                        React.createElement(ResolutionPresetItem_1.ResolutionPresetItem, { width: 800, height: 600, onSelect: this.onSelect }),
                        React.createElement(ResolutionPresetItem_1.ResolutionPresetItem, { width: 1024, height: 768, onSelect: this.onSelect }),
                        React.createElement(ResolutionPresetItem_1.ResolutionPresetItem, { width: 1280, height: 960, onSelect: this.onSelect }),
                        React.createElement(ResolutionPresetItem_1.ResolutionPresetItem, { width: 1280, height: 1024, onSelect: this.onSelect })),
                    React.createElement("div", { className: "col-4" },
                        React.createElement("h6", { className: "dropdown-header" }, "16:9 Ratio"),
                        React.createElement(ResolutionPresetItem_1.ResolutionPresetItem, { width: 854, height: 480, onSelect: this.onSelect, isDefault: true }),
                        React.createElement(ResolutionPresetItem_1.ResolutionPresetItem, { width: 1280, height: 720, onSelect: this.onSelect }),
                        React.createElement(ResolutionPresetItem_1.ResolutionPresetItem, { width: 1360, height: 768, onSelect: this.onSelect }),
                        React.createElement(ResolutionPresetItem_1.ResolutionPresetItem, { width: 1600, height: 900, onSelect: this.onSelect }),
                        React.createElement(ResolutionPresetItem_1.ResolutionPresetItem, { width: 1920, height: 1080, onSelect: this.onSelect })),
                    React.createElement("div", { className: "col-4" },
                        React.createElement("h6", { className: "dropdown-header" }, "16:10 Ratio"),
                        React.createElement(ResolutionPresetItem_1.ResolutionPresetItem, { width: 1280, height: 768, onSelect: this.onSelect }),
                        React.createElement(ResolutionPresetItem_1.ResolutionPresetItem, { width: 1280, height: 800, onSelect: this.onSelect }),
                        React.createElement(ResolutionPresetItem_1.ResolutionPresetItem, { width: 1440, height: 900, onSelect: this.onSelect }),
                        React.createElement(ResolutionPresetItem_1.ResolutionPresetItem, { width: 1600, height: 1024, onSelect: this.onSelect }),
                        React.createElement(ResolutionPresetItem_1.ResolutionPresetItem, { width: 1680, height: 1050, onSelect: this.onSelect }))))));
    }
}
exports.ResolutionPresetMenu = ResolutionPresetMenu;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiUmVzb2x1dGlvblByZXNldE1lbnUuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9zcmMvanMvY29tcG9uZW50cy9zZXR0aW5ncy9SZXNvbHV0aW9uUHJlc2V0TWVudS50c3giXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsK0JBQStCO0FBQy9CLGlFQUE4RDtBQVU5RCxNQUFhLG9CQUFxQixTQUFRLEtBQUssQ0FBQyxTQUF1QjtJQUVuRSxZQUFZLEtBQVk7UUFDcEIsS0FBSyxDQUFDLEtBQUssQ0FBQyxDQUFDO1FBRWIsSUFBSSxDQUFDLEtBQUssR0FBRztZQUNULFFBQVEsRUFBRSxLQUFLO1NBQ2xCLENBQUM7UUFFRixJQUFJLENBQUMsUUFBUSxHQUFHLElBQUksQ0FBQyxRQUFRLENBQUMsSUFBSSxDQUFDLElBQUksQ0FBQyxDQUFDO1FBQ3pDLElBQUksQ0FBQyxNQUFNLEdBQUcsSUFBSSxDQUFDLE1BQU0sQ0FBQyxJQUFJLENBQUMsSUFBSSxDQUFDLENBQUM7UUFDckMsSUFBSSxDQUFDLFlBQVksR0FBRyxJQUFJLENBQUMsWUFBWSxDQUFDLElBQUksQ0FBQyxJQUFJLENBQUMsQ0FBQztRQUNqRCxJQUFJLENBQUMsWUFBWSxHQUFHLElBQUksQ0FBQyxZQUFZLENBQUMsSUFBSSxDQUFDLElBQUksQ0FBQyxDQUFDO0lBQ3JELENBQUM7SUFFRCxRQUFRLENBQUMsS0FBYSxFQUFFLE1BQWM7UUFDbEMsSUFBSSxDQUFDLFFBQVEsQ0FBQyxFQUFFLFFBQVEsRUFBRSxLQUFLLEVBQUUsQ0FBQyxDQUFDO1FBQ25DLElBQUksQ0FBQyxLQUFLLENBQUMsUUFBUSxDQUFDLEtBQUssRUFBRSxNQUFNLENBQUMsQ0FBQztJQUN2QyxDQUFDO0lBRUQsTUFBTTtRQUNGLElBQUksQ0FBQyxRQUFRLENBQUMsRUFBRSxRQUFRLEVBQUUsQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLFFBQVEsRUFBRSxDQUFDLENBQUM7SUFDdEQsQ0FBQztJQUVE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