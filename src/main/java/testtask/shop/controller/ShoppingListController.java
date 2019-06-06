package testtask.shop.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import testtask.shop.exceptions.BadRequestException;
import testtask.shop.model.ShoppingList;
import testtask.shop.service.ShoppingListService;

import java.util.List;

/**
 * Created by Dmitry Martynov on 30.05.2019
 */

@RestController
@RequestMapping("shopping")
@Api(value = "Управление списками покупок", description = "Операции для управления списками покупок")
public class ShoppingListController {

    @Autowired
    private ShoppingListService shoppingListService;

    @GetMapping("/lists")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(value = "Запрос всех списков покупок", notes = "Возвращает все списки покупок из базы данных",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ShoppingList> getAllShoppingLists() {
        return shoppingListService.getAll();
    }

    @GetMapping("/lists/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(value = "Запрос списка покупок из базы данных по Id",
            notes = "Возвращает список покупок с указанным Id, если он существует",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ShoppingList getShoppingListById(@ApiParam(value = "Id списка покупок", required = true) @PathVariable long id) {
        return shoppingListService.getById(id);
    }

    @PostMapping("/lists")
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiOperation(value = "Добавление списка покупок в базу данных",
            notes = "Поле Id у списка покупок и входящих в него записей(ProductRecord) должно быть пустым. " +
                    "Для записей должны быть указаны Id продуктов и количество к покупке. " +
                    "Возвращает добавленный список покупок.",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ShoppingList addShoppingList(@ApiParam(value = "Объект ShoppingList", required = true)
                                            @RequestBody ShoppingList shoppingList) {
        return shoppingListService.addShoppingList(shoppingList);
    }

    @DeleteMapping("/lists/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(value = "Удаление списка покупок по Id", notes = "Удаляет список покупок с Id = {id}, если он существует. " +
            "Так же удаляются все связанные с ним записи(ProductRecord).")
    public String deleteShoppingListById(@ApiParam(value = "Id списка покупок", required = true) @PathVariable long id) {
        shoppingListService.deleteById(id);
        return "ShoppingList is deleted successfully";
    }

    @PutMapping("/lists/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(value = "Обновление списка покупок", notes = "Обновляет список покупок с Id = {id}, если он существует. " +
            "Id списка покупок должен быть указан и соответствовать PathVariable id. " +
            "Для записей должны быть указаны Id продуктов и хранилищ(они совпадают), количество к покупке. " +
            "Возвращает измененный список покупок.",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ShoppingList updateShoppingList(@ApiParam(value = "Id списка покупок", required = true) @PathVariable long id,
                                           @ApiParam(value = "Объект ShoppingList", required = true)@RequestBody ShoppingList shoppingList) {
        if (shoppingList.getId() != id) {
            throw new BadRequestException("ShoppingList.Id does not match the request path");
        }
        return shoppingListService.editShoppingList(shoppingList);
    }

    @GetMapping("/lists/buy/periodic/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(value = "Запуск периодической покупки списка по Id", notes = "Устанавливает списку покупок с Id = {id} " +
            "флаг периодичности и время следующей покупки, если он существует и не является периодическим в данный момент. " +
            "У списка покупок должен быть указан период.")
    public void startPeriodicBuying(@ApiParam(value = "Id списка покупок", required = true) @PathVariable long id) {
        shoppingListService.startPeriodicBuying(id);
    }

    @DeleteMapping("/lists/buy/periodic/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(value = "Остановка периодической покупки списка по Id", notes = "Снимает со списка покупок с Id = {id} " +
            "флаг периодичности и очищает поле nextTimeToBuy, если он существует и является периодическим в данный момент.")
    public void stopPeriodicBuying(@ApiParam(value = "Id списка покупок", required = true) @PathVariable long id) {
        shoppingListService.stopPeriodicBuying(id);
    }

    @GetMapping("/lists/buy/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(value = "Выкуп продуктов по списку один раз", notes = "Совершает выкуп продуктов по списку, если всех продуктов достаточно на складе. " +
            "Результат попытки сохраняется в поле resultOfLastTryToBuy объекта ShoppingList.")
    public void buyShoppingListOnce(@PathVariable long id) {
        shoppingListService.buyShoppingListOnce(id);
    }
}
