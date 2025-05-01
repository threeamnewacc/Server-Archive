"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.setActiveProfile = exports.loadLauncherProfiles = void 0;
const path_1 = require("path");
const fs_1 = require("fs");
const settings_1 = require("./settings");
async function loadLauncherProfiles() {
    if (true) {
        return {
            active: undefined,
            profiles: []
        };
    }
    const filePath = path_1.join(settings_1.getLaunchDirectory(), 'launcher_profiles.json');
    const fileContents = await fs_1.promises.readFile(filePath, 'utf8');
    const fileJson = JSON.parse(fileContents);
    let active = undefined;
    const profiles = [];
    const selectedProfile = fileJson.selectedUser.profile;
    for (const [id, profile] of Object.entries(fileJson.authenticationDatabase)) {
        // @ts-ignore
        for (const [uuid, contents] of Object.entries(profile.profiles)) {
            const profileObj = {
                id: id,
                uuid: uuid,
                // @ts-ignore
                username: contents.displayName
            };
            profiles.push(profileObj);
            if (selectedProfile === profileObj.uuid) {
                active = profileObj;
            }
        }
    }
    return {
        active: active,
        profiles: profiles
    };
}
exports.loadLauncherProfiles = loadLauncherProfiles;
async function setActiveProfile(profile) {
    const filePath = path_1.join(settings_1.getLaunchDirectory(), 'launcher_profiles.json');
    const fileContents = await fs_1.promises.readFile(filePath, 'utf8');
    const fileJson = JSON.parse(fileContents);
    fileJson.selectedUser = {
        account: profile.id,
        profile: profile.uuid
    };
    const newContents = JSON.stringify(fileJson, null, 2);
    await fs_1.promises.writeFile(filePath, newContents);
}
exports.setActiveProfile = setActiveProfile;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoibGF1bmNoZXJQcm9maWxlcy5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uL3NyYy9qcy9sYXVuY2hlclByb2ZpbGVzLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7OztBQUFBLCtCQUE0QjtBQUM1QiwyQkFBb0M7QUFDcEMseUNBQWdEO0FBYXpDLEtBQUssVUFBVSxvQkFBb0I7SUFDdEMsSUFBSSxJQUFJLEVBQUU7UUFDTixPQUFPO1lBQ0gsTUFBTSxFQUFFLFNBQVM7WUFDakIsUUFBUSxFQUFFLEVBQUU7U0FDZixDQUFBO0tBQ0o7SUFFRCxNQUFNLFFBQVEsR0FBRyxXQUFJLENBQUMsNkJBQWtCLEVBQUUsRUFBRSx3QkFBd0IsQ0FBQyxDQUFDO0lBQ3RFLE1BQU0sWUFBWSxHQUFHLE1BQU0sYUFBRSxDQUFDLFFBQVEsQ0FBQyxRQUFRLEVBQUUsTUFBTSxDQUFDLENBQUM7SUFDekQsTUFBTSxRQUFRLEdBQUcsSUFBSSxDQUFDLEtBQUssQ0FBQyxZQUFZLENBQUMsQ0FBQztJQUUxQyxJQUFJLE1BQU0sR0FBWSxTQUFTLENBQUM7SUFDaEMsTUFBTSxRQUFRLEdBQUcsRUFBRSxDQUFDO0lBRXBCLE1BQU0sZUFBZSxHQUFHLFFBQVEsQ0FBQyxZQUFZLENBQUMsT0FBTyxDQUFDO0lBRXRELEtBQUssTUFBTSxDQUFDLEVBQUUsRUFBRSxPQUFPLENBQUMsSUFBSSxNQUFNLENBQUMsT0FBTyxDQUFDLFFBQVEsQ0FBQyxzQkFBc0IsQ0FBQyxFQUFFO1FBQ3pFLGFBQWE7UUFDYixLQUFLLE1BQU0sQ0FBQyxJQUFJLEVBQUUsUUFBUSxDQUFDLElBQUksTUFBTSxDQUFDLE9BQU8sQ0FBQyxPQUFPLENBQUMsUUFBUSxDQUFDLEVBQUU7WUFDN0QsTUFBTSxVQUFVLEdBQUc7Z0JBQ2YsRUFBRSxFQUFFLEVBQUU7Z0JBQ04sSUFBSSxFQUFFLElBQUk7Z0JBQ1YsYUFBYTtnQkFDYixRQUFRLEVBQUUsUUFBUSxDQUFDLFdBQVc7YUFDakMsQ0FBQztZQUVGLFFBQVEsQ0FBQyxJQUFJLENBQUMsVUFBVSxDQUFDLENBQUM7WUFFMUIsSUFBSSxlQUFlLEtBQUssVUFBVSxDQUFDLElBQUksRUFBRTtnQkFDckMsTUFBTSxHQUFHLFVBQVUsQ0FBQzthQUN2QjtTQUNKO0tBQ0o7SUFFRCxPQUFPO1FBQ0gsTUFBTSxFQUFFLE1BQU07UUFDZCxRQUFRLEVBQUUsUUFBUTtLQUNyQixDQUFDO0FBQ04sQ0FBQztBQXZDRCxvREF1Q0M7QUFFTSxLQUFLLFVBQVUsZ0JBQWdCLENBQUMsT0FBZ0I7SUFDbkQsTUFBTSxRQUFRLEdBQUcsV0FBSSxDQUFDLDZCQUFrQixFQUFFLEVBQUUsd0JBQXdCLENBQUMsQ0FBQztJQUN0RSxNQUFNLFlBQVksR0FBRyxNQUFNLGFBQUUsQ0FBQyxRQUFRLENBQUMsUUFBUSxFQUFFLE1BQU0sQ0FBQyxDQUFDO0lBQ3pELE1BQU0sUUFBUSxHQUFHLElBQUksQ0FBQyxLQUFLLENBQUMsWUFBWSxDQUFDLENBQUM7SUFFMUMsUUFBUSxDQUFDLFlBQVksR0FBRztRQUNwQixPQUFPLEVBQUUsT0FBTyxDQUFDLEVBQUU7UUFDbkIsT0FBTyxFQUFFLE9BQU8sQ0FBQyxJQUFJO0tBQ3hCLENBQUM7SUFFRixNQUFNLFdBQVcsR0FBRyxJQUFJLENBQUMsU0FBUyxDQUFDLFFBQVEsRUFBRSxJQUFJLEVBQUUsQ0FBQyxDQUFDLENBQUM7SUFDdEQsTUFBTSxhQUFFLENBQUMsU0FBUyxDQUFDLFFBQVEsRUFBRSxXQUFXLENBQUMsQ0FBQztBQUM5QyxDQUFDO0FBWkQsNENBWUMifQ==