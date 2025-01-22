import {NavigationContainer} from '@react-navigation/native';
import "@/global.css";
import {GluestackUIProvider} from "@/components/ui/gluestack-ui-provider";
import MonitorScreen from "./screens/MonitorScreen";
import {AuthProvider} from "./context/AuthContext";
import HistoryScreen from "./screens/HistoryScreen";
import ProfileScreen from "./screens/ProfileScreen";
import {FontAwesome6, Ionicons} from "@expo/vector-icons";
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import LoginScreen from "./screens/LoginScreen";
import {useEffect} from "react";
import {Alert, BackHandler} from "react-native";
import {createBottomTabNavigator} from "@react-navigation/bottom-tabs";
import RegisterScreen from "@/screens/RegisterScreen";
import {AlertDialog} from "@/components/ui";
import ExitAppDialog from "@/components/ExitAppDialog";

const MainTabs = createBottomTabNavigator();
const Stack = createNativeStackNavigator();

// Tab 导航器
function MainTabsNavigator() {
    return (
        <GluestackUIProvider mode="light">
            <ExitAppDialog/>
            <MainTabs.Navigator
                initialRouteName="Monitor"
                id={undefined}
                screenOptions={{
                    headerShown: false
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
        <GluestackUIProvider mode="light"><AuthProvider>
            <NavigationContainer>
                <Stack.Navigator initialRouteName="MainTabs" id={undefined} screenOptions={{headerShown: false}}>
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
        </AuthProvider></GluestackUIProvider>
    );
}
