"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.LauncherVersionCard = void 0;
const React = require("react");
const constants_1 = require("../../constants");
class LauncherVersionCard extends React.Component {
    render() {
        return (React.createElement("div", { className: "card" },
            React.createElement("h1", { className: "lunar-text" },
                React.createElement("i", { className: "fas fa-code-branch mr-1" }),
                "LAUNCHER VERSION"),
            React.createElement("h4", null,
                "v",
                constants_1.VERSION),
            React.createElement("p", null,
                "Electron: v",
                process.versions.electron,
                " \u2022 Chrome: v",
                process.versions.chrome,
                " \u2022 Node: v",
                process.versions.node)));
    }
}
exports.LauncherVersionCard = LauncherVersionCard;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiTGF1bmNoZXJWZXJzaW9uQ2FyZC5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3NyYy9qcy9jb21wb25lbnRzL2Fib3V0L0xhdW5jaGVyVmVyc2lvbkNhcmQudHN4Il0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7OztBQUFBLCtCQUErQjtBQUMvQiwrQ0FBMEM7QUFFMUMsTUFBYSxtQkFBb0IsU0FBUSxLQUFLLENBQUMsU0FBaUI7SUFFNUQsTUFBTTtRQUNGLE9BQU8sQ0FBQyw2QkFBSyxTQUFTLEVBQUMsTUFBTTtZQUN6Qiw0QkFBSSxTQUFTLEVBQUMsWUFBWTtnQkFDdEIsMkJBQUcsU0FBUyxFQUFDLHlCQUF5QixHQUFHO21DQUV4QztZQUNMOztnQkFBTSxtQkFBTyxDQUFNO1lBQ25COztnQkFBZSxPQUFPLENBQUMsUUFBUSxDQUFDLFFBQVE7O2dCQUFjLE9BQU8sQ0FBQyxRQUFRLENBQUMsTUFBTTs7Z0JBQVksT0FBTyxDQUFDLFFBQVEsQ0FBQyxJQUFJLENBQUssQ0FDakgsQ0FBQyxDQUFDO0lBQ1osQ0FBQztDQUVKO0FBYkQsa0RBYUMifQ==