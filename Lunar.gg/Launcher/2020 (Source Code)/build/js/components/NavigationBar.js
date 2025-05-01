"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.NavigationBar = void 0;
const React = require("react");
const CloseButton_1 = require("./CloseButton");
class NavigationBar extends React.Component {
    render() {
        return (React.createElement("div", { id: "nav" },
            React.createElement("nav", { className: "navbar navbar-dark" },
                React.createElement("div", { className: "container-fluid" },
                    React.createElement("a", { id: "navbar-title", className: "col navbar-brand" },
                        React.createElement("img", { draggable: false, src: "images/logo.png", className: "mr-3", alt: "logo" }),
                        React.createElement("h2", null, "Lunar Client")),
                    React.createElement("div", { id: "nav_links", className: "col-6" }, this.props.tabs.map(t => React.createElement("a", { key: t.name, type: "button", className: 'btn' + (this.props.activeTab === t.name ? ' active' : ''), onClick: t.onClick }, t.name))),
                    React.createElement("div", { id: "navbar-right", className: "col" },
                        React.createElement(CloseButton_1.CloseButton, null))))));
    }
}
exports.NavigationBar = NavigationBar;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiTmF2aWdhdGlvbkJhci5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uL3NyYy9qcy9jb21wb25lbnRzL05hdmlnYXRpb25CYXIudHN4Il0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7OztBQUFBLCtCQUErQjtBQUMvQiwrQ0FBNEM7QUFnQjVDLE1BQWEsYUFBYyxTQUFRLEtBQUssQ0FBQyxTQUFvQjtJQUV6RCxNQUFNO1FBQ0YsT0FBTyxDQUFDLDZCQUFLLEVBQUUsRUFBQyxLQUFLO1lBQ2pCLDZCQUFLLFNBQVMsRUFBQyxvQkFBb0I7Z0JBQy9CLDZCQUFLLFNBQVMsRUFBQyxpQkFBaUI7b0JBQzVCLDJCQUFHLEVBQUUsRUFBQyxjQUFjLEVBQUMsU0FBUyxFQUFDLGtCQUFrQjt3QkFDN0MsNkJBQUssU0FBUyxFQUFFLEtBQUssRUFBRSxHQUFHLEVBQUMsaUJBQWlCLEVBQUMsU0FBUyxFQUFDLE1BQU0sRUFBQyxHQUFHLEVBQUMsTUFBTSxHQUFHO3dCQUMzRSwrQ0FBcUIsQ0FDckI7b0JBQ0osNkJBQUssRUFBRSxFQUFDLFdBQVcsRUFBQyxTQUFTLEVBQUMsT0FBTyxJQUNoQyxJQUFJLENBQUMsS0FBSyxDQUFDLElBQUksQ0FBQyxHQUFHLENBQUMsQ0FBQyxDQUFDLEVBQUUsQ0FBQywyQkFBRyxHQUFHLEVBQUUsQ0FBQyxDQUFDLElBQUksRUFBRSxJQUFJLEVBQUMsUUFBUSxFQUFDLFNBQVMsRUFBRSxLQUFLLEdBQUcsQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLFNBQVMsS0FBSyxDQUFDLENBQUMsSUFBSSxDQUFDLENBQUMsQ0FBQyxTQUFTLENBQUMsQ0FBQyxDQUFDLEVBQUUsQ0FBQyxFQUFFLE9BQU8sRUFBRSxDQUFDLENBQUMsT0FBTyxJQUFHLENBQUMsQ0FBQyxJQUFJLENBQUssQ0FBQyxDQUM5SjtvQkFFTiw2QkFBSyxFQUFFLEVBQUMsY0FBYyxFQUFDLFNBQVMsRUFBQyxLQUFLO3dCQUVsQyxvQkFBQyx5QkFBVyxPQUFHLENBQ2IsQ0FDSixDQUNKLENBQ0osQ0FBQyxDQUFDO0lBQ1osQ0FBQztDQUVKO0FBdkJELHNDQXVCQyJ9