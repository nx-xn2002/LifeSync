import './gesture-handler';
import {NavigationContainer} from '@react-navigation/native';
import "@/global.css";
import {GluestackUIProvider} from "@/components/ui/gluestack-ui-provider";
import MonitorScreen from "./screens/MonitorScreen";
import {AuthProvider} from "./context/AuthContext";
import HistoryScreen from "./screens/HistoryScreen";
import ProfileScreen from "./screens/ProfileScreen";
import {FontAwesome6, Ionicons} from "@expo/vector-icons";
import LoginScreen from "./screens/LoginScreen";
import {createBottomTabNavigator} from "@react-navigation/bottom-tabs";
import RegisterScreen from "@/screens/RegisterScreen";
import ExitAppDialog from "@/components/ExitAppDialog";
import {createStackNavigator} from "@react-navigation/stack";

const MainTabs = createBottomTabNavigator();
const Stack = createStackNavigator ();

// Tab 导航器
function MainTabsNavigator() {
    return (
        <GluestackUIProvider mode="light">
            <ExitAppDialog/>
            <MainTabs.Navigator
                initialRouteName="Monitor"
                id={undefined}
                screenOptions={{
                    headerShown: false,
                    animation: "shift"
                }}
            >
                <MainTabs.Screen
                    name="Monitor"
                    component={MonitorScreen}
                    options={{
                        tabBarIcon: () => <FontAwesome6 name="heart-pulse" size={20}/>,
                    }}
                />
                <MainTabs.Screen
                    name="History"
                    component={HistoryScreen}
                    options={{
                        tabBarIcon: () => <FontAwesome6 name="chart-area" size={20}/>,
                    }}
                />
                <MainTabs.Screen
                    name="Profile"
                    component={ProfileScreen}
                    options={{
                        tabBarIcon: () => <Ionicons name="person" size={20}/>,
                    }}
                />
            </MainTabs.Navigator>
        </GluestackUIProvider>
    );
}

export default function App() {
    return (
        <GluestackUIProvider mode="light">
            <AuthProvider>
                <NavigationContainer>
                    <Stack.Navigator
                        initialRouteName="MainTabs"
                        id={undefined}
                        screenOptions={{
                            headerShown: false,
                            animation: "scale_from_center"
                        }}
                    >
                        {/* 包裹 Tab 导航器 */}
                        <Stack.Screen
                            name="MainTabs"
                            component={MainTabsNavigator}
                        />
                        {/* 添加登录页面 */}
                        <Stack.Screen
                            name="Login"
                            component={LoginScreen}
                        />
                        <Stack.Screen
                            name="Register"
                            component={RegisterScreen}
                        />
                    </Stack.Navigator>
                </NavigationContainer>
            </AuthProvider>
        </GluestackUIProvider>
    );
}
