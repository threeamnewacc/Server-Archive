"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.LegalCard = void 0;
const React = require("react");
const electron_1 = require("electron");
const constants_1 = require("../../constants");
class LegalCard extends React.Component {
    render() {
        return (React.createElement("div", { className: "card" },
            React.createElement("h1", { className: "lunar-text" },
                React.createElement("i", { className: "fas fa-file-contract mr-1" }),
                "LEGAL"),
            React.createElement("button", { type: "button", className: "btn lunar-text aboutButton", onClick: () => electron_1.shell.openExternal(constants_1.TOS_URL) },
                React.createElement("i", { className: "fas fa-gavel mr-1" }),
                "Terms of Service"),
            React.createElement("p", null, "Ensure you are in compliance with our terms of service.")));
    }
}
exports.LegalCard = LegalCard;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiTGVnYWxDYXJkLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vc3JjL2pzL2NvbXBvbmVudHMvYWJvdXQvTGVnYWxDYXJkLnRzeCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFBQSwrQkFBK0I7QUFDL0IsdUNBQWlDO0FBQ2pDLCtDQUEwQztBQUUxQyxNQUFhLFNBQVUsU0FBUSxLQUFLLENBQUMsU0FBaUI7SUFFbEQsTUFBTTtRQUNGLE9BQU8sQ0FBQyw2QkFBSyxTQUFTLEVBQUMsTUFBTTtZQUN6Qiw0QkFBSSxTQUFTLEVBQUMsWUFBWTtnQkFDdEIsMkJBQUcsU0FBUyxFQUFDLDJCQUEyQixHQUFHO3dCQUUxQztZQUNMLGdDQUFRLElBQUksRUFBQyxRQUFRLEVBQUMsU0FBUyxFQUFDLDRCQUE0QixFQUFDLE9BQU8sRUFBRSxHQUFHLEVBQUUsQ0FBQyxnQkFBSyxDQUFDLFlBQVksQ0FBQyxtQkFBTyxDQUFDO2dCQUNuRywyQkFBRyxTQUFTLEVBQUMsbUJBQW1CLEdBQUc7bUNBRTlCO1lBQ1QseUZBQThELENBQzVELENBQUMsQ0FBQztJQUNaLENBQUM7Q0FFSjtBQWhCRCw4QkFnQkMifQ==