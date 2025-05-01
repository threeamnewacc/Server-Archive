"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.AboutPage = void 0;
const React = require("react");
const LegalCard_1 = require("./LegalCard");
const LauncherVersionCard_1 = require("./LauncherVersionCard");
const LogsFolderCard_1 = require("./LogsFolderCard");
const AboutUsCard_1 = require("./AboutUsCard");
const QuickLinksCard_1 = require("./QuickLinksCard");
class AboutPage extends React.Component {
    render() {
        return (React.createElement("div", { id: "about-container", className: "container-fluid" },
            React.createElement("div", { className: "row" },
                React.createElement("div", { id: "about-information", className: "col-12" },
                    React.createElement("div", { className: "card" },
                        React.createElement("div", { id: "about-information-title", className: "card-body lunar-text card-img-top" },
                            React.createElement("h1", null,
                                React.createElement("i", { className: "fas fa-question-circle mr-1" }),
                                "LAUNCHER INFORMATION"),
                            React.createElement("h6", null, "LAUNCHER VERSION, LOGS FOLDER, AND LINKS")),
                        React.createElement("div", { id: "about-information-content", className: "card-body" },
                            React.createElement("div", null,
                                React.createElement(AboutUsCard_1.AboutUsCard, null),
                                React.createElement("div", { className: "row my-3" },
                                    React.createElement("div", { className: "col-4" },
                                        React.createElement(LegalCard_1.LegalCard, null)),
                                    React.createElement("div", { className: "col-4" },
                                        React.createElement(LauncherVersionCard_1.LauncherVersionCard, null)),
                                    React.createElement("div", { className: "col-4" },
                                        React.createElement(LogsFolderCard_1.LogsFolderCard, null))),
                                React.createElement(QuickLinksCard_1.QuickLinksCard, null))))))));
    }
}
exports.AboutPage = AboutPage;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiQWJvdXRQYWdlLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vc3JjL2pzL2NvbXBvbmVudHMvYWJvdXQvQWJvdXRQYWdlLnRzeCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFBQSwrQkFBK0I7QUFDL0IsMkNBQXdDO0FBQ3hDLCtEQUE0RDtBQUM1RCxxREFBa0Q7QUFDbEQsK0NBQTRDO0FBQzVDLHFEQUFrRDtBQUVsRCxNQUFhLFNBQVUsU0FBUSxLQUFLLENBQUMsU0FBaUI7SUFFbEQsTUFBTTtRQUNGLE9BQU8sQ0FBQyw2QkFBSyxFQUFFLEVBQUMsaUJBQWlCLEVBQUMsU0FBUyxFQUFDLGlCQUFpQjtZQUN6RCw2QkFBSyxTQUFTLEVBQUMsS0FBSztnQkFDaEIsNkJBQUssRUFBRSxFQUFDLG1CQUFtQixFQUFDLFNBQVMsRUFBQyxRQUFRO29CQUMxQyw2QkFBSyxTQUFTLEVBQUMsTUFBTTt3QkFDakIsNkJBQUssRUFBRSxFQUFDLHlCQUF5QixFQUFDLFNBQVMsRUFBQyxtQ0FBbUM7NEJBQzNFO2dDQUNJLDJCQUFHLFNBQVMsRUFBQyw2QkFBNkIsR0FBRzt1REFFNUM7NEJBQ0wsMkVBQWlELENBQy9DO3dCQUNOLDZCQUFLLEVBQUUsRUFBQywyQkFBMkIsRUFBQyxTQUFTLEVBQUMsV0FBVzs0QkFDckQ7Z0NBQ0ksb0JBQUMseUJBQVcsT0FBRztnQ0FDZiw2QkFBSyxTQUFTLEVBQUMsVUFBVTtvQ0FDckIsNkJBQUssU0FBUyxFQUFDLE9BQU87d0NBQ2xCLG9CQUFDLHFCQUFTLE9BQUcsQ0FDWDtvQ0FDTiw2QkFBSyxTQUFTLEVBQUMsT0FBTzt3Q0FDbEIsb0JBQUMseUNBQW1CLE9BQUcsQ0FDckI7b0NBQ04sNkJBQUssU0FBUyxFQUFDLE9BQU87d0NBQ2xCLG9CQUFDLCtCQUFjLE9BQUcsQ0FDaEIsQ0FDSjtnQ0FDTixvQkFBQywrQkFBYyxPQUFHLENBQ2hCLENBQ0osQ0FDSixDQUNKLENBQ0osQ0FDSixDQUFDLENBQUM7SUFDWixDQUFDO0NBRUo7QUFyQ0QsOEJBcUNDIn0=