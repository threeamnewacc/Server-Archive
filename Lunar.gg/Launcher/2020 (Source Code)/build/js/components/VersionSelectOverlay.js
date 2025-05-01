"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.VersionSelectOverlay = void 0;
const React = require("react");
const VersionTile_1 = require("./VersionTile");
const settings_1 = require("../settings");
class VersionSelectOverlay extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            selected: settings_1.getSelectedVersion()
        };
        this.escapeButton = this.escapeButton.bind(this);
        this.mouseClicked = this.mouseClicked.bind(this);
    }
    versionSelected(version) {
        this.setState({ selected: version.id });
        settings_1.setSelectedVersion(version.id);
    }
    escapeButton(event) {
        if (event.code === 'Escape') {
            this.props.onExit();
        }
    }
    mouseClicked(event) {
        const versions = document.getElementById('versions');
        if (event.target instanceof Node && !versions.contains(event.target)) {
            this.props.onExit();
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
        return (React.createElement("div", { id: "overlay" },
            React.createElement("div", { className: "container row" },
                React.createElement("div", { id: "launch-options-title", className: "col-12" },
                    React.createElement("h2", null, "LAUNCH OPTIONS"),
                    React.createElement("h5", null, "SELECT A VERSION TO SET A NEW DEFAULT")),
                React.createElement("div", { className: "col-12 row", id: "versions" }, this.props.versions.map(v => React.createElement(VersionTile_1.VersionTile, { key: v.id, version: v, selected: v.id == this.state.selected, onClick: () => this.versionSelected(v) }))),
                React.createElement("div", { className: "col-12", id: "select-version" },
                    React.createElement("a", { className: "btn clickable lunar-text" },
                        React.createElement("i", { className: "fas fa-arrow-alt-circle-right mr-1" }),
                        "Select Version")))));
    }
}
exports.VersionSelectOverlay = VersionSelectOverlay;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiVmVyc2lvblNlbGVjdE92ZXJsYXkuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi9zcmMvanMvY29tcG9uZW50cy9WZXJzaW9uU2VsZWN0T3ZlcmxheS50c3giXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsK0JBQStCO0FBRS9CLCtDQUE0QztBQUM1QywwQ0FBcUU7QUFXckUsTUFBYSxvQkFBcUIsU0FBUSxLQUFLLENBQUMsU0FBdUI7SUFFbkUsWUFBWSxLQUFZO1FBQ3BCLEtBQUssQ0FBQyxLQUFLLENBQUMsQ0FBQztRQUNiLElBQUksQ0FBQyxLQUFLLEdBQUc7WUFDVCxRQUFRLEVBQUUsNkJBQWtCLEVBQUU7U0FDakMsQ0FBQztRQUVGLElBQUksQ0FBQyxZQUFZLEdBQUcsSUFBSSxDQUFDLFlBQVksQ0FBQyxJQUFJLENBQUMsSUFBSSxDQUFDLENBQUM7UUFDakQsSUFBSSxDQUFDLFlBQVksR0FBRyxJQUFJLENBQUMsWUFBWSxDQUFDLElBQUksQ0FBQyxJQUFJLENBQUMsQ0FBQztJQUNyRCxDQUFDO0lBRUQsZUFBZSxDQUFDLE9BQWdCO1FBQzVCLElBQUksQ0FBQyxRQUFRLENBQUMsRUFBRSxRQUFRLEVBQUUsT0FBTyxDQUFDLEVBQUUsRUFBRSxDQUFDLENBQUM7UUFDeEMsNkJBQWtCLENBQUMsT0FBTyxDQUFDLEVBQUUsQ0FBQyxDQUFDO0lBQ25DLENBQUM7SUFFRCxZQUFZLENBQUMsS0FBb0I7UUFDN0IsSUFBSSxLQUFLLENBQUMsSUFBSSxLQUFLLFFBQVEsRUFBRTtZQUN6QixJQUFJLENBQUMsS0FBSyxDQUFDLE1BQU0sRUFBRSxDQUFDO1NBQ3ZCO0lBQ0wsQ0FBQztJQUVELFlBQVksQ0FBQyxLQUFpQjtRQUMxQixNQUFNLFFBQVEsR0FBRyxRQUFRLENBQUMsY0FBYyxDQUFDLFVBQVUsQ0FBQyxDQUFDO1FBRXJELElBQUksS0FBSyxDQUFDLE1BQU0sWUFBWSxJQUFJLElBQUksQ0FBQyxRQUFRLENBQUMsUUFBUSxDQUFDLEtBQUssQ0FBQyxNQUFNLENBQUMsRUFBRTtZQUNsRSxJQUFJLENBQUMsS0FBSyxDQUFDLE1BQU0sRUFBRSxDQUFDO1NBQ3ZCO0lBQ0wsQ0FBQztJQUVELGlCQUFpQjtRQUNiLFFBQVEsQ0FBQyxnQkFBZ0IsQ0FBQyxTQUFTLEVBQUUsSUFBSSxDQUFDLFlBQVksRUFBRSxLQUFLLENBQUMsQ0FBQztRQUMvRCxRQUFRLENBQUMsZ0JBQWdCLENBQUMsT0FBTyxFQUFFLElBQUksQ0FBQyxZQUFZLEVBQUUsSUFBSSxDQUFDLENBQUM7SUFDaEUsQ0FBQztJQUVELG9CQUFvQjtRQUNoQixRQUFRLENBQUMsbUJBQW1CLENBQUMsU0FBUyxFQUFFLElBQUksQ0FBQyxZQUFZLEVBQUUsS0FBSyxDQUFDLENBQUM7UUFDbEUsUUFBUSxDQUFDLG1CQUFtQixDQUFDLE9BQU8sRUFBRSxJQUFJLENBQUMsWUFBWSxFQUFFLElBQUksQ0FBQyxDQUFDO0lBQ25FLENBQUM7SUFFRCxNQUFNO1FBQ0YsT0FBTyxDQUFDLDZCQUFLLEVBQUUsRUFBQyxTQUFTO1lBQ3JCLDZCQUFLLFNBQVMsRUFBQyxlQUFlO2dCQUMxQiw2QkFBSyxFQUFFLEVBQUMsc0JBQXNCLEVBQUMsU0FBUyxFQUFDLFFBQVE7b0JBQzdDLGlEQUF1QjtvQkFDdkIsd0VBQThDLENBQzVDO2dCQUNOLDZCQUFLLFNBQVMsRUFBQyxZQUFZLEVBQUMsRUFBRSxFQUFDLFVBQVUsSUFDcEMsSUFBSSxDQUFDLEtBQUssQ0FBQyxRQUFRLENBQUMsR0FBRyxDQUFDLENBQUMsQ0FBQyxFQUFFLENBQUMsb0JBQUMseUJBQVcsSUFBQyxHQUFHLEVBQUUsQ0FBQyxDQUFDLEVBQUUsRUFDaEQsT0FBTyxFQUFFLENBQUMsRUFDVixRQUFRLEVBQUUsQ0FBQyxDQUFDLEVBQUUsSUFBSSxJQUFJLENBQUMsS0FBSyxDQUFDLFFBQVEsRUFDckMsT0FBTyxFQUFFLEdBQUcsRUFBRSxDQUFDLElBQUksQ0FBQyxlQUFlLENBQUMsQ0FBQyxDQUFDLEdBQ3hDLENBQUMsQ0FDRDtnQkFDTiw2QkFBSyxTQUFTLEVBQUMsUUFBUSxFQUFDLEVBQUUsRUFBQyxnQkFBZ0I7b0JBQ3ZDLDJCQUFHLFNBQVMsRUFBQywwQkFBMEI7d0JBQ25DLDJCQUFHLFNBQVMsRUFBQyxvQ0FBb0MsR0FBRzt5Q0FFcEQsQ0FDRixDQUNKLENBQ0osQ0FBQyxDQUFDO0lBQ1osQ0FBQztDQUVKO0FBakVELG9EQWlFQyJ9