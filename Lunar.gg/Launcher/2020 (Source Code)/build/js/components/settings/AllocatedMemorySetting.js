"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.AllocatedMemorySetting = void 0;
const React = require("react");
const settings_1 = require("../../settings");
class AllocatedMemorySetting extends React.Component {
    constructor(props) {
        super(props);
        this.pendingSave = null;
        this.state = {
            allocatedMemoryMb: settings_1.getAllocatedMemory()
        };
    }
    sliderChanged(event) {
        let memoryMb = parseInt(event.target.value);
        this.setState({ allocatedMemoryMb: memoryMb });
        // delete old save task if changed again
        if (this.pendingSave != null)
            clearTimeout(this.pendingSave);
        // save in 0.5s
        this.pendingSave = setTimeout(() => settings_1.setAllocatedMemory(memoryMb), 500);
    }
    render() {
        let allocatedGb = this.state.allocatedMemoryMb / 1024;
        let remainingGb = (this.props.maxMemoryMb - this.state.allocatedMemoryMb) / 1024;
        return (React.createElement("div", { className: "setting-category" },
            React.createElement("h1", { className: "lunar-text" },
                React.createElement("i", { className: "fas fa-sliders-h mr-1" }),
                "Allocated Memory"),
            React.createElement("h5", null, "How much memory should we allocate to the game"),
            React.createElement("input", { type: "range", className: "custom-range py-3", min: this.props.minMemoryMb, value: this.state.allocatedMemoryMb, max: this.props.maxMemoryMb, onChange: e => this.sliderChanged(e) }),
            React.createElement("h3", null,
                allocatedGb.toFixed(1),
                "GB Allocated"),
            React.createElement("h4", null,
                "You have ",
                remainingGb.toFixed(1),
                "GB left to allocate")));
    }
}
exports.AllocatedMemorySetting = AllocatedMemorySetting;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiQWxsb2NhdGVkTWVtb3J5U2V0dGluZy5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3NyYy9qcy9jb21wb25lbnRzL3NldHRpbmdzL0FsbG9jYXRlZE1lbW9yeVNldHRpbmcudHN4Il0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7OztBQUFBLCtCQUErQjtBQUMvQiw2Q0FBd0U7QUFXeEUsTUFBYSxzQkFBdUIsU0FBUSxLQUFLLENBQUMsU0FBdUI7SUFJckUsWUFBWSxLQUFZO1FBQ3BCLEtBQUssQ0FBQyxLQUFLLENBQUMsQ0FBQztRQUhULGdCQUFXLEdBQW1CLElBQUksQ0FBQztRQUl2QyxJQUFJLENBQUMsS0FBSyxHQUFHO1lBQ1QsaUJBQWlCLEVBQUUsNkJBQWtCLEVBQUU7U0FDMUMsQ0FBQztJQUNOLENBQUM7SUFFRCxhQUFhLENBQUMsS0FBMEM7UUFDcEQsSUFBSSxRQUFRLEdBQUcsUUFBUSxDQUFDLEtBQUssQ0FBQyxNQUFNLENBQUMsS0FBSyxDQUFDLENBQUM7UUFDNUMsSUFBSSxDQUFDLFFBQVEsQ0FBQyxFQUFFLGlCQUFpQixFQUFFLFFBQVEsRUFBRSxDQUFDLENBQUM7UUFFL0Msd0NBQXdDO1FBQ3hDLElBQUksSUFBSSxDQUFDLFdBQVcsSUFBSSxJQUFJO1lBQUUsWUFBWSxDQUFDLElBQUksQ0FBQyxXQUFXLENBQUMsQ0FBQztRQUU3RCxlQUFlO1FBQ2YsSUFBSSxDQUFDLFdBQVcsR0FBRyxVQUFVLENBQUMsR0FBRyxFQUFFLENBQUMsNkJBQWtCLENBQUMsUUFBUSxDQUFDLEVBQUUsR0FBRyxDQUFDLENBQUM7SUFDM0UsQ0FBQztJQUVELE1BQU07UUFDRixJQUFJLFdBQVcsR0FBRyxJQUFJLENBQUMsS0FBSyxDQUFDLGlCQUFpQixHQUFHLElBQUksQ0FBQztRQUN0RCxJQUFJLFdBQVcsR0FBRyxDQUFDLElBQUksQ0FBQyxLQUFLLENBQUMsV0FBVyxHQUFHLElBQUksQ0FBQyxLQUFLLENBQUMsaUJBQWlCLENBQUMsR0FBRyxJQUFJLENBQUM7UUFFakYsT0FBTyxDQUFDLDZCQUFLLFNBQVMsRUFBQyxrQkFBa0I7WUFDckMsNEJBQUksU0FBUyxFQUFDLFlBQVk7Z0JBQ3RCLDJCQUFHLFNBQVMsRUFBQyx1QkFBdUIsR0FBRzttQ0FFdEM7WUFDTCxpRkFBdUQ7WUFDdkQsK0JBQU8sSUFBSSxFQUFDLE9BQU8sRUFBQyxTQUFTLEVBQUMsbUJBQW1CLEVBQUMsR0FBRyxFQUFFLElBQUksQ0FBQyxLQUFLLENBQUMsV0FBVyxFQUFFLEtBQUssRUFBRSxJQUFJLENBQUMsS0FBSyxDQUFDLGlCQUFpQixFQUFFLEdBQUcsRUFBRSxJQUFJLENBQUMsS0FBSyxDQUFDLFdBQVcsRUFBRSxRQUFRLEVBQUUsQ0FBQyxDQUFDLEVBQUUsQ0FBQyxJQUFJLENBQUMsYUFBYSxDQUFDLENBQUMsQ0FBQyxHQUFJO1lBQ3pMO2dCQUFLLFdBQVcsQ0FBQyxPQUFPLENBQUMsQ0FBQyxDQUFDOytCQUFrQjtZQUM3Qzs7Z0JBQWMsV0FBVyxDQUFDLE9BQU8sQ0FBQyxDQUFDLENBQUM7c0NBQXlCLENBQzNELENBQUMsQ0FBQztJQUNaLENBQUM7Q0FFSjtBQXRDRCx3REFzQ0MifQ==