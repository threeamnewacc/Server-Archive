"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.CloseButton = void 0;
const electron_1 = require("electron");
const React = require("react");
class CloseButton extends React.Component {
    render() {
        return (React.createElement("a", { id: "exit_button", type: "button", className: "btn active ml-2", onClick: () => electron_1.remote.getCurrentWindow().close() },
            React.createElement("i", { className: "fas fa-times" })));
    }
}
exports.CloseButton = CloseButton;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiQ2xvc2VCdXR0b24uanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi9zcmMvanMvY29tcG9uZW50cy9DbG9zZUJ1dHRvbi50c3giXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsdUNBQWtDO0FBQ2xDLCtCQUErQjtBQUUvQixNQUFhLFdBQVksU0FBUSxLQUFLLENBQUMsU0FBaUI7SUFFcEQsTUFBTTtRQUNGLE9BQU8sQ0FBQywyQkFBRyxFQUFFLEVBQUMsYUFBYSxFQUFDLElBQUksRUFBQyxRQUFRLEVBQUMsU0FBUyxFQUFDLGlCQUFpQixFQUFDLE9BQU8sRUFBRSxHQUFHLEVBQUUsQ0FBQyxpQkFBTSxDQUFDLGdCQUFnQixFQUFFLENBQUMsS0FBSyxFQUFFO1lBQ2xILDJCQUFHLFNBQVMsRUFBQyxjQUFjLEdBQUcsQ0FDOUIsQ0FBQyxDQUFDO0lBQ1YsQ0FBQztDQUVKO0FBUkQsa0NBUUMifQ==