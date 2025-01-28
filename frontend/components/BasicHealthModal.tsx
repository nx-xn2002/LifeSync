import React, {useContext, useEffect} from "react";
import {
    Box,
    CloseIcon,
    Heading,
    HStack,
    Icon,
    Modal,
    ModalBackdrop,
    ModalBody,
    ModalCloseButton,
    ModalContent,
    ModalHeader,
    Pressable,
    Text,
    VStack,
} from "./ui";
import {BasicHealthContext} from "@/context/BasicHealthContext";
import {selectBasicHealth, updateBasicHealth} from "@/services/api";
import MaterialCommunityIcons from '@expo/vector-icons/MaterialCommunityIcons';
import MaterialIcons from '@expo/vector-icons/MaterialIcons';

export default function BasicHealthModal({modalVisible, setModalVisible}: any) {
    const {basicHealth, storeBasicHealth} = useContext(BasicHealthContext);
    const fetchData = async () => {
        try {
            const response = await selectBasicHealth();
            if (response.data) {
                storeBasicHealth(response.data);
            }
        } catch (error) {
            console.error("Error fetching basic health data:", error);
        }
    };
    const updateTest = async () => {
        try {
            const response = await updateBasicHealth({
                height: 150,
                weight: 80,
                age: 18,
                gender: 0,
            });
            if (response.data) {
                console.log("update basic health data:", response.data);
            }
        } catch (error) {
            console.error("Error fetching basic health data:", error);
        }
    }
    useEffect(() => {
        if (modalVisible) {
            fetchData();
        } else {
            updateTest();
        }
    }, [modalVisible]);

    return (
        <Box>
            <Modal
                size="md"
                isOpen={modalVisible}
                onClose={() => {
                    setModalVisible(false);
                }}
                avoidKeyboard
            >
                <ModalBackdrop/>
                <ModalContent className="p-4">
                    <ModalHeader>
                        <HStack className="items-center">
                            <Heading size="sm" className="font-semibold">
                                Personal Info
                            </Heading>
                        </HStack>
                        <ModalCloseButton>
                            <Icon
                                as={CloseIcon}
                                className="w-4 h-4"
                                color={"#404040"}
                            />
                        </ModalCloseButton>
                    </ModalHeader>
                    <ModalBody className="mb-0">
                        <VStack space="md">
                            <VStack space="lg">
                                <Pressable>
                                    <HStack className="justify-between">
                                        <HStack space="md">
                                            <Icon
                                                as={() => <MaterialCommunityIcons name="human-male-height" size={24}
                                                                                  color="black"/>}/>
                                            <Text>Height</Text>
                                        </HStack>
                                        <HStack>
                                            <Text>{basicHealth?.height}</Text>
                                        </HStack>
                                    </HStack>
                                </Pressable>
                                <Pressable>
                                    <HStack className="justify-between">
                                        <HStack space="md">
                                            <Icon as={() => <MaterialCommunityIcons name="weight-kilogram" size={24}
                                                                                    color="black"/>}/>
                                            <Text>Weight</Text>
                                        </HStack>
                                        <HStack>
                                            <Text>{basicHealth?.weight}</Text>
                                        </HStack>
                                    </HStack>
                                </Pressable>
                                <Pressable>
                                    <HStack className="justify-between">
                                        <HStack space="md">
                                            <Icon as={() => <MaterialCommunityIcons name="human-handsup" size={24}
                                                                                    color="black"/>}/>
                                            <Text>Age</Text>
                                        </HStack>
                                        <HStack>
                                            <Text>{basicHealth?.age}</Text>
                                        </HStack>
                                    </HStack>
                                </Pressable>
                                <Pressable>
                                    <HStack className="justify-between">
                                        <HStack space="md">
                                            <Icon as={() => {
                                                if (basicHealth?.gender === 0) {
                                                    return <MaterialCommunityIcons name="gender-male" size={24}
                                                                                   color="black"/>
                                                } else if (basicHealth?.gender === 1) {
                                                    return <MaterialCommunityIcons name="gender-female" size={24}
                                                                                   color="black"/>
                                                } else {
                                                    return <MaterialIcons name="question-mark" size={24} color="black"/>
                                                }
                                            }}
                                            />
                                            <Text>Gender</Text>
                                        </HStack>
                                        <HStack>
                                            <Text>{basicHealth?.gender}</Text>
                                        </HStack>
                                    </HStack>
                                </Pressable>
                            </VStack>
                        </VStack>
                    </ModalBody>
                </ModalContent>
            </Modal>
        </Box>
    );
};