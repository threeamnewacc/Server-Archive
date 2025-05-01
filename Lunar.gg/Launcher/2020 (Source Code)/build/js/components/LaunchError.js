"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.LaunchError = void 0;
const React = require("react");
class LaunchError extends React.Component {
    render() {
        return (React.createElement("div", { id: "launchError" },
            React.createElement("div", { className: "container row" },
                React.createElement("div", { className: "modal-content" },
                    React.createElement("div", { className: "modal-header" },
                        React.createElement("h5", { className: "modal-title" },
                            React.createElement("i", { className: "fas fa-exclamation-triangle mr-2" }),
                            "LAUNCH FAILURE"),
                        React.createElement("button", { type: "button", className: "close" },
                            React.createElement("span", null, "\u2212"))),
                    React.createElement("div", { className: "modal-body" },
                        React.createElement("h1", { id: "errorTitle" }, this.props.title),
                        React.createElement("h3", { id: "errorDescription" }, this.props.description),
                        React.createElement("h5", null, "If you're running into this issue regularly, please contact support with this error.")),
                    React.createElement("div", { className: "modal-footer" },
                        React.createElement("button", { type: "button", className: "btn lunar-text aboutButton" },
                            React.createElement("i", { className: "fas fa-copy mr-1" }),
                            "Copy Launch Error"),
                        React.createElement("button", { type: "button", className: "btn lunar-text aboutButton" },
                            React.createElement("i", { className: "fas fa-ticket-alt mr-1" }),
                            "Contact Support"))))));
    }
}
exports.LaunchError = LaunchError;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiTGF1bmNoRXJyb3IuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi9zcmMvanMvY29tcG9uZW50cy9MYXVuY2hFcnJvci50c3giXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsK0JBQStCO0FBTy9CLE1BQWEsV0FBWSxTQUFRLEtBQUssQ0FBQyxTQUFvQjtJQUV2RCxNQUFNO1FBQ0YsT0FBTyxDQUFDLDZCQUFLLEVBQUUsRUFBQyxhQUFhO1lBQ3pCLDZCQUFLLFNBQVMsRUFBQyxlQUFlO2dCQUMxQiw2QkFBSyxTQUFTLEVBQUMsZUFBZTtvQkFDMUIsNkJBQUssU0FBUyxFQUFDLGNBQWM7d0JBQ3pCLDRCQUFJLFNBQVMsRUFBQyxhQUFhOzRCQUN2QiwyQkFBRyxTQUFTLEVBQUMsa0NBQWtDLEdBQUc7NkNBRWpEO3dCQUNMLGdDQUFRLElBQUksRUFBQyxRQUFRLEVBQUMsU0FBUyxFQUFDLE9BQU87NEJBQ25DLDJDQUFvQixDQUNmLENBQ1A7b0JBQ04sNkJBQUssU0FBUyxFQUFDLFlBQVk7d0JBQ3ZCLDRCQUFJLEVBQUUsRUFBQyxZQUFZLElBQUUsSUFBSSxDQUFDLEtBQUssQ0FBQyxLQUFLLENBQU07d0JBQzNDLDRCQUFJLEVBQUUsRUFBQyxrQkFBa0IsSUFBRSxJQUFJLENBQUMsS0FBSyxDQUFDLFdBQVcsQ0FBTTt3QkFDdkQsdUhBQTZGLENBQzNGO29CQUNOLDZCQUFLLFNBQVMsRUFBQyxjQUFjO3dCQUN6QixnQ0FBUSxJQUFJLEVBQUMsUUFBUSxFQUFDLFNBQVMsRUFBQyw0QkFBNEI7NEJBQ3hELDJCQUFHLFNBQVMsRUFBQyxrQkFBa0IsR0FBRztnREFFN0I7d0JBQ1QsZ0NBQVEsSUFBSSxFQUFDLFFBQVEsRUFBQyxTQUFTLEVBQUMsNEJBQTRCOzRCQUN4RCwyQkFBRyxTQUFTLEVBQUMsd0JBQXdCLEdBQUc7OENBRW5DLENBQ1AsQ0FDSixDQUNKLENBQ0osQ0FBQyxDQUFDO0lBQ1osQ0FBQztDQUVKO0FBbkNELGtDQW1DQyJ9