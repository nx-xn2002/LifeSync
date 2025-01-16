import {NavigationContainer} from '@react-navigation/native';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import MonitorScreen from "./screens/MonitorScreen";
import {AuthProvider} from "./context/AuthContext";
import HistoryScreen from "./screens/HistoryScreen";
import ProfileScreen from "./screens/ProfileScreen";
import {FontAwesome6, Ionicons} from "@expo/vector-icons";

const MainTabs = createBottomTabNavigator();
export default function App() {
    return (
        <AuthProvider>
            <NavigationContainer>
                <MainTabs.Navigator initialRouteName="Monitor" id="1">
                    <MainTabs.Screen
                        name="Monitor"
                        component={MonitorScreen}
                        options={{
                            title: 'Monitor',
                            tabBarIcon: () => <FontAwesome6 name="heart-pulse" size={20}/>,
                        }}
                    />
                    <MainTabs.Screen
                        name="History"
                        component={HistoryScreen}
                        options={{
                            title: 'History',
                            tabBarIcon: () => <FontAwesome6 name="chart-area" size={20}/>,
                        }}
                    />
                    <MainTabs.Screen
                        name="Profile"
                        component={ProfileScreen}
                        options={{
                            title: 'Profile',
                            tabBarIcon: () => <Ionicons name="person" size={20}/>,
                        }}
                    />
                </MainTabs.Navigator>
            </NavigationContainer>
        </AuthProvider>
    );
}
