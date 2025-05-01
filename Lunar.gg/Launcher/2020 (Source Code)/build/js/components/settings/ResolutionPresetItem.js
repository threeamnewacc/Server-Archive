"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ResolutionPresetItem = void 0;
const React = require("react");
class ResolutionPresetItem extends React.Component {
    onClick() {
        this.props.onSelect(this.props.width, this.props.height);
    }
    render() {
        return (React.createElement("a", { className: "dropdown-item", onClick: () => this.onClick() },
            this.props.width,
            "x",
            this.props.height,
            this.props.isDefault === true ? React.createElement("span", { style: { fontSize: 'x-small' } }, " (Default)") : null));
    }
}
exports.ResolutionPresetItem = ResolutionPresetItem;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiUmVzb2x1dGlvblByZXNldEl0ZW0uanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9zcmMvanMvY29tcG9uZW50cy9zZXR0aW5ncy9SZXNvbHV0aW9uUHJlc2V0SXRlbS50c3giXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsK0JBQStCO0FBUy9CLE1BQWEsb0JBQXFCLFNBQVEsS0FBSyxDQUFDLFNBQW9CO0lBRWhFLE9BQU87UUFDSCxJQUFJLENBQUMsS0FBSyxDQUFDLFFBQVEsQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLEtBQUssRUFBRSxJQUFJLENBQUMsS0FBSyxDQUFDLE1BQU0sQ0FBQyxDQUFDO0lBQzdELENBQUM7SUFFRCxNQUFNO1FBQ0YsT0FBTyxDQUFDLDJCQUFHLFNBQVMsRUFBQyxlQUFlLEVBQUMsT0FBTyxFQUFFLEdBQUcsRUFBRSxDQUFDLElBQUksQ0FBQyxPQUFPLEVBQUU7WUFDN0QsSUFBSSxDQUFDLEtBQUssQ0FBQyxLQUFLOztZQUFHLElBQUksQ0FBQyxLQUFLLENBQUMsTUFBTTtZQUNwQyxJQUFJLENBQUMsS0FBSyxDQUFDLFNBQVMsS0FBSyxJQUFJLENBQUMsQ0FBQyxDQUFDLDhCQUFNLEtBQUssRUFBRSxFQUFFLFFBQVEsRUFBRSxTQUFTLEVBQUUsaUJBQW1CLENBQUMsQ0FBQyxDQUFDLElBQUksQ0FDL0YsQ0FBQyxDQUFDO0lBQ1YsQ0FBQztDQUVKO0FBYkQsb0RBYUMifQ==