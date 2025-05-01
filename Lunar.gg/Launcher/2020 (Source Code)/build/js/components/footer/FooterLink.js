"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.FooterLink = void 0;
const React = require("react");
const electron_1 = require("electron");
class FooterLink extends React.Component {
    render() {
        return (React.createElement("a", { className: "clickable", onClick: () => electron_1.shell.openExternal(this.props.link) },
            React.createElement("i", { className: "fa-lg " + this.props.icon })));
    }
}
exports.FooterLink = FooterLink;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiRm9vdGVyTGluay5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3NyYy9qcy9jb21wb25lbnRzL2Zvb3Rlci9Gb290ZXJMaW5rLnRzeCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFBQSwrQkFBK0I7QUFDL0IsdUNBQWlDO0FBT2pDLE1BQWEsVUFBVyxTQUFRLEtBQUssQ0FBQyxTQUFvQjtJQUV0RCxNQUFNO1FBQ0YsT0FBTyxDQUFDLDJCQUFHLFNBQVMsRUFBQyxXQUFXLEVBQUMsT0FBTyxFQUFFLEdBQUcsRUFBRSxDQUFDLGdCQUFLLENBQUMsWUFBWSxDQUFDLElBQUksQ0FBQyxLQUFLLENBQUMsSUFBSSxDQUFDO1lBQy9FLDJCQUFHLFNBQVMsRUFBRSxRQUFRLEdBQUcsSUFBSSxDQUFDLEtBQUssQ0FBQyxJQUFJLEdBQUksQ0FDNUMsQ0FBQyxDQUFDO0lBQ1YsQ0FBQztDQUVKO0FBUkQsZ0NBUUMifQ==