"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ProfileSelector = void 0;
const React = require("react");
const launcherProfiles_1 = require("../launcherProfiles");
class ProfileSelector extends React.Component {
    constructor(props) {
        super(props);
        var active = props.launcherProfiles.active;
        if (active === undefined) {
            active = {
                id: 'unknown',
                uuid: 'c06f89064c8a49119c29ea1dbd1aab82',
                username: 'Unknown'
            };
        }
        this.state = {
            expanded: false,
            active: active
        };
        this.escapeButton = this.escapeButton.bind(this);
        this.mouseClicked = this.mouseClicked.bind(this);
    }
    toggleClicked() {
        this.setState({ expanded: !this.state.expanded });
    }
    async profileClicked(profile) {
        this.setState({ active: profile });
        await launcherProfiles_1.setActiveProfile(profile);
    }
    escapeButton(event) {
        if (event.code === 'Escape') {
            this.setState({ expanded: false });
        }
    }
    mouseClicked() {
        this.setState({ expanded: false });
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
        return (React.createElement("div", { id: "profileSelector", className: "btn-group dropdown text-right" },
            React.createElement("a", { id: "selectedAccount", type: "button", className: "btn username-select lunar-text", onClick: () => this.toggleClicked() },
                React.createElement("img", { draggable: false, alt: this.state.active.username, className: "mr-2", src: "https://cravatar.eu/avatar/" + this.state.active.uuid }),
                this.state.active.username),
            React.createElement("div", { id: "profileSelectorDropdown", className: "dropdown-menu dropdown-menu-right" + (this.state.expanded ? " show" : "") }, this.props.launcherProfiles.profiles.map(p => React.createElement("a", { key: p.uuid, className: "dropdown-item lunar-text", onClick: () => this.profileClicked(p) },
                React.createElement("img", { draggable: false, alt: p.username, className: "mr-2", src: "https://cravatar.eu/avatar/" + p.uuid }),
                p.username)))));
    }
}
exports.ProfileSelector = ProfileSelector;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiUHJvZmlsZVNlbGVjdG9yLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL2pzL2NvbXBvbmVudHMvUHJvZmlsZVNlbGVjdG9yLnRzeCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFBQSwrQkFBK0I7QUFDL0IsMERBQWtGO0FBV2xGLE1BQWEsZUFBZ0IsU0FBUSxLQUFLLENBQUMsU0FBdUI7SUFFOUQsWUFBWSxLQUFZO1FBQ3BCLEtBQUssQ0FBQyxLQUFLLENBQUMsQ0FBQztRQUNiLElBQUksTUFBTSxHQUFHLEtBQUssQ0FBQyxnQkFBZ0IsQ0FBQyxNQUFNLENBQUM7UUFFM0MsSUFBSSxNQUFNLEtBQUssU0FBUyxFQUFFO1lBQ3RCLE1BQU0sR0FBRztnQkFDTCxFQUFFLEVBQUUsU0FBUztnQkFDYixJQUFJLEVBQUUsa0NBQWtDO2dCQUN4QyxRQUFRLEVBQUUsU0FBUzthQUN0QixDQUFBO1NBQ0o7UUFFRCxJQUFJLENBQUMsS0FBSyxHQUFHO1lBQ1QsUUFBUSxFQUFFLEtBQUs7WUFDZixNQUFNLEVBQUUsTUFBTTtTQUNqQixDQUFDO1FBRUYsSUFBSSxDQUFDLFlBQVksR0FBRyxJQUFJLENBQUMsWUFBWSxDQUFDLElBQUksQ0FBQyxJQUFJLENBQUMsQ0FBQztRQUNqRCxJQUFJLENBQUMsWUFBWSxHQUFHLElBQUksQ0FBQyxZQUFZLENBQUMsSUFBSSxDQUFDLElBQUksQ0FBQyxDQUFDO0lBQ3JELENBQUM7SUFFRCxhQUFhO1FBQ1QsSUFBSSxDQUFDLFFBQVEsQ0FBQyxFQUFFLFFBQVEsRUFBRSxDQUFDLElBQUksQ0FBQyxLQUFLLENBQUMsUUFBUSxFQUFFLENBQUMsQ0FBQztJQUN0RCxDQUFDO0lBRUQsS0FBSyxDQUFDLGNBQWMsQ0FBQyxPQUFnQjtRQUNqQyxJQUFJLENBQUMsUUFBUSxDQUFDLEVBQUUsTUFBTSxFQUFFLE9BQU8sRUFBRSxDQUFDLENBQUM7UUFDbkMsTUFBTSxtQ0FBZ0IsQ0FBQyxPQUFPLENBQUMsQ0FBQTtJQUNuQyxDQUFDO0lBRUQsWUFBWSxDQUFDLEtBQW9CO1FBQzdCLElBQUksS0FBSyxDQUFDLElBQUksS0FBSyxRQUFRLEVBQUU7WUFDekIsSUFBSSxDQUFDLFFBQVEsQ0FBQyxFQUFFLFFBQVEsRUFBRSxLQUFLLEVBQUUsQ0FBQyxDQUFDO1NBQ3RDO0lBQ0wsQ0FBQztJQUVELFlBQVk7UUFDUixJQUFJLENBQUMsUUFBUSxDQUFDLEVBQUUsUUFBUSxFQUFFLEtBQUssRUFBRSxDQUFDLENBQUM7SUFDdkMsQ0FBQztJQUVELGlCQUFpQjtRQUNiLFFBQVEsQ0FBQyxnQkFBZ0IsQ0FBQyxTQUFTLEVBQUUsSUFBSSxDQUFDLFlBQVksRUFBRSxLQUFLLENBQUMsQ0FBQztRQUMvRCxRQUFRLENBQUMsZ0JBQWdCLENBQUMsT0FBTyxFQUFFLElBQUksQ0FBQyxZQUFZLEVBQUUsSUFBSSxDQUFDLENBQUM7SUFDaEUsQ0FBQztJQUVELG9CQUFvQjtRQUNoQixRQUFRLENBQUMsbUJBQW1CLENBQUMsU0FBUyxFQUFFLElBQUksQ0FBQyxZQUFZLEVBQUUsS0FBSyxDQUFDLENBQUM7UUFDbEUsUUFBUSxDQUFDLG1CQUFtQixDQUFDLE9BQU8sRUFBRSxJQUFJLENBQUMsWUFBWSxFQUFFLElBQUksQ0FBQyxDQUFDO0lBQ25FLENBQUM7SUFFRCxNQUFNO1FBQ0YsT0FBTyxDQUFDLDZCQUFLLEVBQUUsRUFBQyxpQkFBaUIsRUFBQyxTQUFTLEVBQUMsK0JBQStCO1lBQ3ZFLDJCQUFHLEVBQUUsRUFBQyxpQkFBaUIsRUFBQyxJQUFJLEVBQUMsUUFBUSxFQUFDLFNBQVMsRUFBQyxnQ0FBZ0MsRUFBQyxPQUFPLEVBQUUsR0FBRyxFQUFFLENBQUMsSUFBSSxDQUFDLGFBQWEsRUFBRTtnQkFDaEgsNkJBQUssU0FBUyxFQUFFLEtBQUssRUFBRSxHQUFHLEVBQUUsSUFBSSxDQUFDLEtBQUssQ0FBQyxNQUFNLENBQUMsUUFBUSxFQUFFLFNBQVMsRUFBQyxNQUFNLEVBQUMsR0FBRyxFQUFFLDZCQUE2QixHQUFHLElBQUksQ0FBQyxLQUFLLENBQUMsTUFBTSxDQUFDLElBQUksR0FBSTtnQkFDdkksSUFBSSxDQUFDLEtBQUssQ0FBQyxNQUFNLENBQUMsUUFBUSxDQUMzQjtZQUNKLDZCQUFLLEVBQUUsRUFBQyx5QkFBeUIsRUFBQyxTQUFTLEVBQUUsbUNBQW1DLEdBQUcsQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLFFBQVEsQ0FBQyxDQUFDLENBQUMsT0FBTyxDQUFDLENBQUMsQ0FBQyxFQUFFLENBQUMsSUFDbEgsSUFBSSxDQUFDLEtBQUssQ0FBQyxnQkFBZ0IsQ0FBQyxRQUFRLENBQUMsR0FBRyxDQUFDLENBQUMsQ0FBQyxFQUFFLENBQUMsMkJBQUcsR0FBRyxFQUFFLENBQUMsQ0FBQyxJQUFJLEVBQUUsU0FBUyxFQUFDLDBCQUEwQixFQUFDLE9BQU8sRUFBRSxHQUFHLEVBQUUsQ0FBQyxJQUFJLENBQUMsY0FBYyxDQUFDLENBQUMsQ0FBQztnQkFDckksNkJBQUssU0FBUyxFQUFFLEtBQUssRUFBRSxHQUFHLEVBQUUsQ0FBQyxDQUFDLFFBQVEsRUFBRSxTQUFTLEVBQUMsTUFBTSxFQUFDLEdBQUcsRUFBRSw2QkFBNkIsR0FBRyxDQUFDLENBQUMsSUFBSSxHQUFJO2dCQUN2RyxDQUFDLENBQUMsUUFBUSxDQUNYLENBQUMsQ0FDSCxDQUNKLENBQUMsQ0FBQztJQUNaLENBQUM7Q0FFSjtBQW5FRCwwQ0FtRUMifQ==