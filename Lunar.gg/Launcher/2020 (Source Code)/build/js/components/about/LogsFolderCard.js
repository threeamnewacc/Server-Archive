"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.LogsFolderCard = void 0;
const React = require("react");
const electron_1 = require("electron");
const log = require("electron-log");
class LogsFolderCard extends React.Component {
    render() {
        const logFile = log.transports.file.getFile().path;
        const fileBrowserName = process.platform === 'darwin' ? 'Finder' : 'File Explorer';
        return (React.createElement("div", { className: "card" },
            React.createElement("h1", { className: "lunar-text" },
                React.createElement("i", { className: "fas fa-folder mr-1" }),
                "LOGS FOLDER"),
            React.createElement("button", { type: "button", className: "btn lunar-text aboutButton", onClick: () => electron_1.shell.showItemInFolder(logFile) },
                React.createElement("i", { className: "fas fa-folder-open mr-1" }),
                "Open in ",
                fileBrowserName),
            React.createElement("p", null, "Having trouble launching? Send us your logs using the support system!")));
    }
}
exports.LogsFolderCard = LogsFolderCard;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiTG9nc0ZvbGRlckNhcmQuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9zcmMvanMvY29tcG9uZW50cy9hYm91dC9Mb2dzRm9sZGVyQ2FyZC50c3giXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsK0JBQStCO0FBQy9CLHVDQUFpQztBQUNqQyxvQ0FBcUM7QUFFckMsTUFBYSxjQUFlLFNBQVEsS0FBSyxDQUFDLFNBQWlCO0lBRXZELE1BQU07UUFDRixNQUFNLE9BQU8sR0FBRyxHQUFHLENBQUMsVUFBVSxDQUFDLElBQUksQ0FBQyxPQUFPLEVBQUUsQ0FBQyxJQUFJLENBQUM7UUFDbkQsTUFBTSxlQUFlLEdBQUcsT0FBTyxDQUFDLFFBQVEsS0FBSyxRQUFRLENBQUMsQ0FBQyxDQUFDLFFBQVEsQ0FBQyxDQUFDLENBQUMsZUFBZSxDQUFDO1FBRW5GLE9BQU8sQ0FBQyw2QkFBSyxTQUFTLEVBQUMsTUFBTTtZQUN6Qiw0QkFBSSxTQUFTLEVBQUMsWUFBWTtnQkFDdEIsMkJBQUcsU0FBUyxFQUFDLG9CQUFvQixHQUFHOzhCQUVuQztZQUNMLGdDQUFRLElBQUksRUFBQyxRQUFRLEVBQUMsU0FBUyxFQUFDLDRCQUE0QixFQUFDLE9BQU8sRUFBRSxHQUFHLEVBQUUsQ0FBQyxnQkFBSyxDQUFDLGdCQUFnQixDQUFDLE9BQU8sQ0FBQztnQkFDdkcsMkJBQUcsU0FBUyxFQUFDLHlCQUF5QixHQUFHOztnQkFDaEMsZUFBZSxDQUNuQjtZQUNULHVHQUE0RSxDQUMxRSxDQUFDLENBQUM7SUFDWixDQUFDO0NBRUo7QUFuQkQsd0NBbUJDIn0=