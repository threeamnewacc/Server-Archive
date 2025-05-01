"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.HomePage = void 0;
const React = require("react");
const BlogPostTile_1 = require("./BlogPostTile");
const QuickServer_1 = require("./QuickServer");
const MAX_QUICK_SERVERS = 9;
class HomePage extends React.Component {
    render() {
        return (React.createElement("div", { id: "home-container" },
            React.createElement("div", { id: "quick-servers" },
                React.createElement("div", { id: "inner-quick-servers", className: "container-fluid" }, this.props.servers.slice(0, MAX_QUICK_SERVERS).map(s => React.createElement(QuickServer_1.QuickServer, { key: s.name, server: s, onClick: () => this.props.serversOnClick(s) })))),
            React.createElement("h5", null, "Recent News"),
            React.createElement("div", { id: "newsContainer", className: "container-fluid" },
                React.createElement("div", { id: "postsContainer", className: "row" }, this.props.blogPosts.map(p => React.createElement(BlogPostTile_1.BlogPostTile, { key: p.title, blogPost: p }))))));
    }
}
exports.HomePage = HomePage;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiSG9tZVBhZ2UuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9zcmMvanMvY29tcG9uZW50cy9ob21lL0hvbWVQYWdlLnRzeCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFBQSwrQkFBK0I7QUFFL0IsaURBQThDO0FBQzlDLCtDQUE0QztBQUU1QyxNQUFNLGlCQUFpQixHQUFHLENBQUMsQ0FBQztBQVE1QixNQUFhLFFBQVMsU0FBUSxLQUFLLENBQUMsU0FBb0I7SUFFcEQsTUFBTTtRQUNGLE9BQU8sQ0FBQyw2QkFBSyxFQUFFLEVBQUMsZ0JBQWdCO1lBQzVCLDZCQUFLLEVBQUUsRUFBQyxlQUFlO2dCQUNuQiw2QkFBSyxFQUFFLEVBQUMscUJBQXFCLEVBQUMsU0FBUyxFQUFDLGlCQUFpQixJQUNwRCxJQUFJLENBQUMsS0FBSyxDQUFDLE9BQU8sQ0FBQyxLQUFLLENBQUMsQ0FBQyxFQUFFLGlCQUFpQixDQUFDLENBQUMsR0FBRyxDQUFDLENBQUMsQ0FBQyxFQUFFLENBQUMsb0JBQUMseUJBQVcsSUFBQyxHQUFHLEVBQUUsQ0FBQyxDQUFDLElBQUksRUFBRSxNQUFNLEVBQUUsQ0FBQyxFQUFFLE9BQU8sRUFBRSxHQUFHLEVBQUUsQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLGNBQWMsQ0FBQyxDQUFDLENBQUMsR0FBSSxDQUFDLENBQzVJLENBQ0o7WUFFTiw4Q0FBb0I7WUFDcEIsNkJBQUssRUFBRSxFQUFDLGVBQWUsRUFBQyxTQUFTLEVBQUMsaUJBQWlCO2dCQUMvQyw2QkFBSyxFQUFFLEVBQUMsZ0JBQWdCLEVBQUMsU0FBUyxFQUFDLEtBQUssSUFDbkMsSUFBSSxDQUFDLEtBQUssQ0FBQyxTQUFTLENBQUMsR0FBRyxDQUFDLENBQUMsQ0FBQyxFQUFFLENBQUMsb0JBQUMsMkJBQVksSUFBQyxHQUFHLEVBQUUsQ0FBQyxDQUFDLEtBQUssRUFBRSxRQUFRLEVBQUUsQ0FBQyxHQUFJLENBQUMsQ0FDekUsQ0FDSixDQUNKLENBQUMsQ0FBQztJQUNaLENBQUM7Q0FFSjtBQW5CRCw0QkFtQkMifQ==