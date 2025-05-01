"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.VersionTile = void 0;
const React = require("react");
const settings_1 = require("../settings");
const aal_1 = require("../lib/aal");
class VersionTile extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            anticheat: settings_1.isAnticheatEnabled(this.props.version.id)
        };
    }
    anticheatToggled() {
        let nextState = !this.state.anticheat;
        // if we can't run the anticheat, it's always set to off
        if (!aal_1.isCompatibleWithSystem()) {
            nextState = false;
        }
        this.setState({ anticheat: nextState });
        settings_1.setAnticheatEnabled(this.props.version.id, nextState);
    }
    render() {
        return (React.createElement("div", { className: "col-6" },
            React.createElement("div", { className: "card version-card" },
                React.createElement("div", { className: "wrapper clickable", onClick: () => this.props.onClick() },
                    React.createElement("div", { className: "version-description card-body px-5 lunar-text " + (this.props.selected ? "selected-version" : ""), style: { backgroundImage: "url('" + this.props.version.image + "')" } },
                        React.createElement("h1", null,
                            "Version ",
                            this.props.version.name),
                        React.createElement("h6", null,
                            "LAST UPDATED ",
                            this.props.version.lastUpdated.toUpperCase()))),
                React.createElement("div", { className: "card-body" },
                    React.createElement("div", { className: "btn-group version-ac-button", onClick: () => this.anticheatToggled() },
                        React.createElement("button", { className: "btn lunar-text " + (this.state.anticheat ? "verACOn" : "verACOff") },
                            React.createElement("h5", null, "ANTICHEAT")),
                        React.createElement("button", { className: "btn lunar-text version-ac-right " + (this.state.anticheat ? "verACOn" : "verACOff") },
                            React.createElement("h5", { className: "version-ac-status" }, this.state.anticheat ? "ON" : "OFF")))))));
    }
}
exports.VersionTile = VersionTile;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiVmVyc2lvblRpbGUuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi9zcmMvanMvY29tcG9uZW50cy9WZXJzaW9uVGlsZS50c3giXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsK0JBQStCO0FBRS9CLDBDQUFzRTtBQUN0RSxvQ0FBb0Q7QUFZcEQsTUFBYSxXQUFZLFNBQVEsS0FBSyxDQUFDLFNBQXVCO0lBRTFELFlBQVksS0FBWTtRQUNwQixLQUFLLENBQUMsS0FBSyxDQUFDLENBQUM7UUFDYixJQUFJLENBQUMsS0FBSyxHQUFHO1lBQ1QsU0FBUyxFQUFFLDZCQUFrQixDQUFDLElBQUksQ0FBQyxLQUFLLENBQUMsT0FBTyxDQUFDLEVBQUUsQ0FBQztTQUN2RCxDQUFDO0lBQ04sQ0FBQztJQUVELGdCQUFnQjtRQUNaLElBQUksU0FBUyxHQUFHLENBQUMsSUFBSSxDQUFDLEtBQUssQ0FBQyxTQUFTLENBQUM7UUFFdEMsd0RBQXdEO1FBQ3hELElBQUksQ0FBQyw0QkFBc0IsRUFBRSxFQUFFO1lBQzNCLFNBQVMsR0FBRyxLQUFLLENBQUM7U0FDckI7UUFFRCxJQUFJLENBQUMsUUFBUSxDQUFDLEVBQUUsU0FBUyxFQUFFLFNBQVMsRUFBRSxDQUFDLENBQUM7UUFDeEMsOEJBQW1CLENBQUMsSUFBSSxDQUFDLEtBQUssQ0FBQyxPQUFPLENBQUMsRUFBRSxFQUFFLFNBQVMsQ0FBQyxDQUFDO0lBQzFELENBQUM7SUFFRCxNQUFNO1FBQ0YsT0FBTyxDQUFDLDZCQUFLLFNBQVMsRUFBQyxPQUFPO1lBQzFCLDZCQUFLLFNBQVMsRUFBQyxtQkFBbUI7Z0JBQzlCLDZCQUFLLFNBQVMsRUFBQyxtQkFBbUIsRUFBQyxPQUFPLEVBQUUsR0FBRyxFQUFFLENBQUMsSUFBSSxDQUFDLEtBQUssQ0FBQyxPQUFPLEVBQUU7b0JBQ2xFLDZCQUFLLFNBQVMsRUFBRSxnREFBZ0QsR0FBRyxDQUFDLElBQUksQ0FBQyxLQUFLLENBQUMsUUFBUSxDQUFDLENBQUMsQ0FBQyxrQkFBa0IsQ0FBQyxDQUFDLENBQUMsRUFBRSxDQUFDLEVBQUUsS0FBSyxFQUFFLEVBQUUsZUFBZSxFQUFFLE9BQU8sR0FBRyxJQUFJLENBQUMsS0FBSyxDQUFDLE9BQU8sQ0FBQyxLQUFLLEdBQUcsSUFBSSxFQUFFO3dCQUNyTDs7NEJBQWEsSUFBSSxDQUFDLEtBQUssQ0FBQyxPQUFPLENBQUMsSUFBSSxDQUFNO3dCQUMxQzs7NEJBQWtCLElBQUksQ0FBQyxLQUFLLENBQUMsT0FBTyxDQUFDLFdBQVcsQ0FBQyxXQUFXLEVBQUUsQ0FBTSxDQUNsRSxDQUNKO2dCQUVOLDZCQUFLLFNBQVMsRUFBQyxXQUFXO29CQUN0Qiw2QkFBSyxTQUFTLEVBQUMsNkJBQTZCLEVBQUMsT0FBTyxFQUFFLEdBQUcsRUFBRSxDQUFDLElBQUksQ0FBQyxnQkFBZ0IsRUFBRTt3QkFDL0UsZ0NBQVEsU0FBUyxFQUFFLGlCQUFpQixHQUFHLENBQUMsSUFBSSxDQUFDLEtBQUssQ0FBQyxTQUFTLENBQUMsQ0FBQyxDQUFDLFNBQVMsQ0FBQyxDQUFDLENBQUMsVUFBVSxDQUFDOzRCQUNsRiw0Q0FBa0IsQ0FDYjt3QkFDVCxnQ0FBUSxTQUFTLEVBQUUsa0NBQWtDLEdBQUcsQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLFNBQVMsQ0FBQyxDQUFDLENBQUMsU0FBUyxDQUFDLENBQUMsQ0FBQyxVQUFVLENBQUM7NEJBQ25HLDRCQUFJLFNBQVMsRUFBQyxtQkFBbUIsSUFBRSxJQUFJLENBQUMsS0FBSyxDQUFDLFNBQVMsQ0FBQyxDQUFDLENBQUMsSUFBSSxDQUFDLENBQUMsQ0FBQyxLQUFLLENBQU0sQ0FDdkUsQ0FDUCxDQUNKLENBQ0osQ0FDSixDQUFDLENBQUM7SUFDWixDQUFDO0NBRUo7QUE3Q0Qsa0NBNkNDIn0=