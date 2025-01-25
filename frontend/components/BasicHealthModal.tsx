import React, {useContext, useEffect, useState} from "react";
import {
    Box,
    Button,
    ButtonText,
    CircleIcon,
    CloseIcon,
    FormControl,
    FormControlLabel,
    FormControlLabelText,
    Heading,
    HStack,
    Icon,
    Modal,
    ModalBackdrop,
    ModalBody,
    ModalCloseButton,
    ModalContent,
    ModalHeader,
    Radio,
    RadioGroup,
    RadioIcon,
    RadioIndicator,
    RadioLabel,
    VStack,
} from "./ui";
import {AuthContext} from "@/context/AuthContext";
import {BasicHealthContext} from "@/context/BasicHealthContext";
import {selectBasicHealth} from "@/services/api";


const propertyType = [
    "Flat/Apartment",
    "Independent House / Villa",
    "Independent Floor/Builder Floor",
    "Plot / Land",
];
const sellOrRentOptions = ["Sell", "Rent/Lease"];

const BasicHealthModal = ({modalVisible, setModalVisible}: any) => {
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
    useEffect(() => {
        if (modalVisible) {
            fetchData();
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
                        <ModalContent1/>
                    </ModalBody>
                </ModalContent>
            </Modal>
        </Box>
    );
};

const ModalContent1 = () => {
    const [values, setValues]: any = React.useState("Residential");
    const [selectedSellOrRentOption, setSelectedSellOrRentOption] = useState(
        sellOrRentOptions[0]
    );
    const [selectedPropertyTypeOptions, setSelectedPropertyTypeOptions]: any =
        useState([]);
    const colorMode = "light";

    const handlePropertyTypeSelection = (item: any) => {
        if (selectedPropertyTypeOptions.includes(item)) {
            setSelectedPropertyTypeOptions(
                selectedPropertyTypeOptions.filter((option: string) => option !== item)
            );
        } else {
            setSelectedPropertyTypeOptions([...selectedPropertyTypeOptions, item]);
        }
    };
    return (
        <VStack space="md">
            <VStack space="sm">
                <FormControl>
                    <FormControlLabel>
                        <FormControlLabelText>I want to...</FormControlLabelText>
                    </FormControlLabel>
                    <HStack space="sm">
                        {sellOrRentOptions.map((item, index) => (
                            <Button
                                key={index}
                                action={
                                    item === selectedSellOrRentOption ? "primary" : "secondary"
                                }
                                variant="outline"
                                size="xs"
                                onPress={() => {
                                    setSelectedSellOrRentOption(item);
                                }}
                                className="rounded-full mb-2"
                            >
                                <ButtonText>{item}</ButtonText>
                            </Button>
                        ))}
                    </HStack>
                </FormControl>
            </VStack>
            <VStack space="md">
                <VStack space="sm">
                    <FormControl>
                        <FormControlLabel>
                            <FormControlLabelText>Property is...</FormControlLabelText>
                        </FormControlLabel>
                        <RadioGroup
                            value={values}
                            onChange={setValues}
                            accessibilityLabel="place-type"
                        >
                            <HStack space="md">
                                <Radio value="Residential">
                                    <RadioIndicator>
                                        <RadioIcon
                                            as={CircleIcon}
                                            color={colorMode === "light" ? "#292929" : "#FAFAFA"}
                                        />
                                    </RadioIndicator>
                                    <RadioLabel>Residential</RadioLabel>
                                </Radio>
                                <Radio value="Commercial">
                                    <RadioIndicator>
                                        <RadioIcon
                                            as={CircleIcon}
                                            color={colorMode === "light" ? "#292929" : "#FAFAFA"}
                                        />
                                    </RadioIndicator>
                                    <RadioLabel>Commercial</RadioLabel>
                                </Radio>
                            </HStack>
                        </RadioGroup>
                    </FormControl>
                </VStack>
                <HStack space="sm" className="flex-wrap">
                    {propertyType.map((item: string, index: any) => (
                        <Button
                            key={index}
                            action={
                                selectedPropertyTypeOptions.includes(item)
                                    ? "primary"
                                    : "secondary"
                            }
                            variant="outline"
                            size="xs"
                            onPress={() => {
                                handlePropertyTypeSelection(item);
                            }}
                            className="rounded-full mb-2 hover:bg-background-200"
                        >
                            <ButtonText>{item}</ButtonText>
                        </Button>
                    ))}
                </HStack>
            </VStack>
        </VStack>
    );
};
export default BasicHealthModal;