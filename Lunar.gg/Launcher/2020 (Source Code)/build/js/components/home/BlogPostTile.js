"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.BlogPostTile = void 0;
const React = require("react");
const electron_1 = require("electron");
class BlogPostTile extends React.Component {
    render() {
        return (React.createElement("div", { className: "col-4" },
            React.createElement("div", { className: "card post-card" },
                React.createElement("div", { className: "inner" },
                    React.createElement("img", { draggable: false, className: "card-img-top", src: this.props.blogPost.image, alt: this.props.blogPost.title })),
                React.createElement("div", { className: "card-body rounded" },
                    React.createElement("p", { className: "card-text" }, this.props.blogPost.excerpt),
                    React.createElement("h6", { className: "card-subtitle mb-2" },
                        "Posted by",
                        React.createElement("img", { draggable: false, className: "ml-2 mr-1", src: "http://cravatar.eu/avatar/" + this.props.blogPost.author }),
                        React.createElement("span", { className: "author" }, this.props.blogPost.author)),
                    React.createElement("a", { className: "read-more card-link lunar-text clickable", onClick: () => electron_1.shell.openExternal(this.props.blogPost.link) },
                        React.createElement("i", { className: "fas fa-book mr-1" }),
                        "Read more")))));
    }
}
exports.BlogPostTile = BlogPostTile;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiQmxvZ1Bvc3RUaWxlLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vc3JjL2pzL2NvbXBvbmVudHMvaG9tZS9CbG9nUG9zdFRpbGUudHN4Il0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7OztBQUFBLCtCQUErQjtBQUUvQix1Q0FBaUM7QUFNakMsTUFBYSxZQUFhLFNBQVEsS0FBSyxDQUFDLFNBQW9CO0lBRXhELE1BQU07UUFDRixPQUFPLENBQUMsNkJBQUssU0FBUyxFQUFDLE9BQU87WUFDMUIsNkJBQUssU0FBUyxFQUFDLGdCQUFnQjtnQkFDM0IsNkJBQUssU0FBUyxFQUFDLE9BQU87b0JBQ2xCLDZCQUFLLFNBQVMsRUFBRSxLQUFLLEVBQUUsU0FBUyxFQUFDLGNBQWMsRUFBQyxHQUFHLEVBQUUsSUFBSSxDQUFDLEtBQUssQ0FBQyxRQUFRLENBQUMsS0FBSyxFQUFFLEdBQUcsRUFBRSxJQUFJLENBQUMsS0FBSyxDQUFDLFFBQVEsQ0FBQyxLQUFLLEdBQUksQ0FDaEg7Z0JBQ04sNkJBQUssU0FBUyxFQUFDLG1CQUFtQjtvQkFDOUIsMkJBQUcsU0FBUyxFQUFDLFdBQVcsSUFBRSxJQUFJLENBQUMsS0FBSyxDQUFDLFFBQVEsQ0FBQyxPQUFPLENBQUs7b0JBQzFELDRCQUFJLFNBQVMsRUFBQyxvQkFBb0I7O3dCQUU5Qiw2QkFBSyxTQUFTLEVBQUUsS0FBSyxFQUFFLFNBQVMsRUFBQyxXQUFXLEVBQUMsR0FBRyxFQUFFLDRCQUE0QixHQUFHLElBQUksQ0FBQyxLQUFLLENBQUMsUUFBUSxDQUFDLE1BQU0sR0FBSTt3QkFDL0csOEJBQU0sU0FBUyxFQUFDLFFBQVEsSUFBRSxJQUFJLENBQUMsS0FBSyxDQUFDLFFBQVEsQ0FBQyxNQUFNLENBQVEsQ0FDM0Q7b0JBQ0wsMkJBQUcsU0FBUyxFQUFDLDBDQUEwQyxFQUFDLE9BQU8sRUFBRSxHQUFHLEVBQUUsQ0FBQyxnQkFBSyxDQUFDLFlBQVksQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLFFBQVEsQ0FBQyxJQUFJLENBQUM7d0JBQy9HLDJCQUFHLFNBQVMsRUFBQyxrQkFBa0IsR0FBRztvQ0FFbEMsQ0FDRixDQUNKLENBQ0osQ0FBQyxDQUFDO0lBQ1osQ0FBQztDQUVKO0FBeEJELG9DQXdCQyJ9