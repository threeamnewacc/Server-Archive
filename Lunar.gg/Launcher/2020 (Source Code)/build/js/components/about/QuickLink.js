"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.QuickLink = void 0;
const React = require("react");
const electron_1 = require("electron");
class QuickLink extends React.Component {
    render() {
        return (React.createElement("button", { className: "btn lunar-text", onClick: () => electron_1.shell.openExternal(this.props.link) },
            React.createElement("i", { className: "mr-1 fas " + this.props.icon }),
            this.props.name));
    }
}
exports.QuickLink = QuickLink;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiUXVpY2tMaW5rLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vc3JjL2pzL2NvbXBvbmVudHMvYWJvdXQvUXVpY2tMaW5rLnRzeCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFBQSwrQkFBK0I7QUFDL0IsdUNBQWlDO0FBUWpDLE1BQWEsU0FBVSxTQUFRLEtBQUssQ0FBQyxTQUFvQjtJQUVyRCxNQUFNO1FBQ0YsT0FBTyxDQUFDLGdDQUFRLFNBQVMsRUFBQyxnQkFBZ0IsRUFBQyxPQUFPLEVBQUUsR0FBRyxFQUFFLENBQUMsZ0JBQUssQ0FBQyxZQUFZLENBQUMsSUFBSSxDQUFDLEtBQUssQ0FBQyxJQUFJLENBQUM7WUFDekYsMkJBQUcsU0FBUyxFQUFFLFdBQVcsR0FBRyxJQUFJLENBQUMsS0FBSyxDQUFDLElBQUksR0FBSTtZQUM5QyxJQUFJLENBQUMsS0FBSyxDQUFDLElBQUksQ0FDWCxDQUFDLENBQUM7SUFDZixDQUFDO0NBRUo7QUFURCw4QkFTQyJ9