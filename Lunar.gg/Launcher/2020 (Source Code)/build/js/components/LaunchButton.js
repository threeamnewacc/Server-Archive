"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.LaunchButton = void 0;
const React = require("react");
class LaunchButton extends React.Component {
    render() {
        // update touch bar button
        const touchBar = this.props.touchBarButton;
        touchBar.label = this.props.text + ' ' + {
            ready: '🚀',
            working: '🔨',
            error: '⚠️'
        }[this.props.state];
        touchBar.backgroundColor = {
            ready: '#179d44',
            working: '#800080',
            error: '#db4040'
        }[this.props.state];
        touchBar.enabled = this.props.state === 'ready';
        let dropdown = this.props.state === 'ready';
        return (React.createElement("div", { id: "launch", style: { height: this.props.height + "vh" } },
            React.createElement("div", { id: "launchBtn", className: this.props.state },
                React.createElement("div", { className: "btn-group" },
                    React.createElement("button", { type: "button", role: "group", className: "btn lunar-text py-2 px-0", onClick: () => this.props.buttonOnClick() },
                        React.createElement("div", { className: "inner-text" },
                            React.createElement("h2", { id: "launchVersion" }, this.props.text),
                            React.createElement("p", { id: "anticheatStatus" }, this.props.subtext))),
                    dropdown ? React.createElement("button", { type: "button", className: "btn", onClick: () => this.props.dropdownOnClick() },
                        React.createElement("i", { className: "fas fa-caret-down" })) : null))));
    }
}
exports.LaunchButton = LaunchButton;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiTGF1bmNoQnV0dG9uLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL2pzL2NvbXBvbmVudHMvTGF1bmNoQnV0dG9uLnRzeCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFBQSwrQkFBK0I7QUFjL0IsTUFBYSxZQUFhLFNBQVEsS0FBSyxDQUFDLFNBQW9CO0lBRXhELE1BQU07UUFDRiwwQkFBMEI7UUFDMUIsTUFBTSxRQUFRLEdBQUcsSUFBSSxDQUFDLEtBQUssQ0FBQyxjQUFjLENBQUM7UUFFM0MsUUFBUSxDQUFDLEtBQUssR0FBRyxJQUFJLENBQUMsS0FBSyxDQUFDLElBQUksR0FBRyxHQUFHLEdBQUc7WUFDckMsS0FBSyxFQUFFLElBQUk7WUFDWCxPQUFPLEVBQUUsSUFBSTtZQUNiLEtBQUssRUFBRSxJQUFJO1NBQ2QsQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLEtBQUssQ0FBQyxDQUFDO1FBQ3BCLFFBQVEsQ0FBQyxlQUFlLEdBQUc7WUFDdkIsS0FBSyxFQUFFLFNBQVM7WUFDaEIsT0FBTyxFQUFFLFNBQVM7WUFDbEIsS0FBSyxFQUFFLFNBQVM7U0FDbkIsQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLEtBQUssQ0FBQyxDQUFDO1FBQ3BCLFFBQVEsQ0FBQyxPQUFPLEdBQUcsSUFBSSxDQUFDLEtBQUssQ0FBQyxLQUFLLEtBQUssT0FBTyxDQUFDO1FBRWhELElBQUksUUFBUSxHQUFHLElBQUksQ0FBQyxLQUFLLENBQUMsS0FBSyxLQUFLLE9BQU8sQ0FBQztRQUU1QyxPQUFPLENBQUMsNkJBQUssRUFBRSxFQUFDLFFBQVEsRUFBQyxLQUFLLEVBQUUsRUFBRSxNQUFNLEVBQUUsSUFBSSxDQUFDLEtBQUssQ0FBQyxNQUFNLEdBQUcsSUFBSSxFQUFFO1lBQ2hFLDZCQUFLLEVBQUUsRUFBQyxXQUFXLEVBQUMsU0FBUyxFQUFFLElBQUksQ0FBQyxLQUFLLENBQUMsS0FBSztnQkFDM0MsNkJBQUssU0FBUyxFQUFDLFdBQVc7b0JBQ3RCLGdDQUFRLElBQUksRUFBQyxRQUFRLEVBQUMsSUFBSSxFQUFDLE9BQU8sRUFBQyxTQUFTLEVBQUMsMEJBQTBCLEVBQUMsT0FBTyxFQUFFLEdBQUcsRUFBRSxDQUFDLElBQUksQ0FBQyxLQUFLLENBQUMsYUFBYSxFQUFFO3dCQUM3Ryw2QkFBSyxTQUFTLEVBQUMsWUFBWTs0QkFDdkIsNEJBQUksRUFBRSxFQUFDLGVBQWUsSUFBRSxJQUFJLENBQUMsS0FBSyxDQUFDLElBQUksQ0FBTTs0QkFDN0MsMkJBQUcsRUFBRSxFQUFDLGlCQUFpQixJQUFFLElBQUksQ0FBQyxLQUFLLENBQUMsT0FBTyxDQUFLLENBQzlDLENBQ0Q7b0JBQ1IsUUFBUSxDQUFDLENBQUMsQ0FBQyxnQ0FBUSxJQUFJLEVBQUMsUUFBUSxFQUFDLFNBQVMsRUFBQyxLQUFLLEVBQUMsT0FBTyxFQUFFLEdBQUcsRUFBRSxDQUFDLElBQUksQ0FBQyxLQUFLLENBQUMsZUFBZSxFQUFFO3dCQUN6RiwyQkFBRyxTQUFTLEVBQUMsbUJBQW1CLEdBQUcsQ0FDOUIsQ0FBQyxDQUFDLENBQUMsSUFBSSxDQUNkLENBQ0osQ0FDSixDQUFDLENBQUM7SUFDWixDQUFDO0NBRUo7QUFyQ0Qsb0NBcUNDIn0=