import {NavigationContainer} from '@react-navigation/native';
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

const MainTabs = createBottomTabNavigator();
const Stack = createNativeStackNavigator();

// Tab 导航器
function MainTabsNavigator() {
  useEffect(() => {
    const backAction = () => {
      Alert.alert('Exit App', 'Do you want to exit the app?', [
        {
          text: 'Cancel',
          onPress: () => null,
          style: 'cancel',
        },
        {
          text: 'Yes',
          onPress: () => BackHandler.exitApp(),
        },
      ]);
      return true; // 阻止默认行为
    };
    const backHandler = BackHandler.addEventListener('hardwareBackPress', backAction);
    return () => backHandler.remove();
  }, []);
  return (
      <MainTabs.Navigator initialRouteName="Monitor">
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
  );
}

export default function App() {
  return (
      <AuthProvider>
        <NavigationContainer>
          <Stack.Navigator initialRouteName="MainTabs">
            {/* 包裹 Tab 导航器 */}
            <Stack.Screen
                name="MainTabs"
                component={MainTabsNavigator}
                options={{headerShown: false}} // 隐藏顶部导航栏
            />
            {/* 添加登录页面 */}
            <Stack.Screen
                name="Login"
                component={LoginScreen}
                options={{title: 'Login'}}
            />
          </Stack.Navigator>
        </NavigationContainer>
      </AuthProvider>
  );
}
