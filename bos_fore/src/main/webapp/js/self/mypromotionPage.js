bosfore_app.controller('ctrlRead',["$scope",'$http',function($scope,$http){
	//当前页数，初始化为1
	$scope.currentPage = 1;
	//每页记录数
	$scope.pageSize=3;
	//总记录数
	$scope.totalCount=0;
	//总页数，用来编写页码
	$scope.totalPages=0;
	
	$scope.prev=function(){
		if($scope.currentPage>1){
			$scope.selectPage($scope.currentPage-1);
		}
	};
	
	$scope.next=function(){
		if($scope.currentPage<$scope.totalPages){
			$scope.selectPage($scope.currentPage+1);
		}
	};
	$scope.selectPage=function(page){
		 // 如果页码超出范围
        if ($scope.totalPages != 0) {
            if (page < 1 || page > $scope.totalPages) return;
        }

		//获取分页数据
		$http({
			method:'GET',
			url:'promotion_pageQuery.action',
			params:{
				"page":page,
				"rows":$scope.pageSize
			}
		}).error(function(data, status, headers, config){
			//当响应以错误状态返回时调用
            alert("出错，请联系管理员 ");
		}).success(function(response){
			/*
			 * response：{
			 * 				"total":总记录数,
			 * 				"pageData":[{promotion},{},{},{}]
			 * 			}
			 */
			//接收promotion集合json
			$scope.pageItems=response.dataPage;
			$scope.totalCount=response.totalCount;
			$scope.totalPages=Math.ceil($scope.totalCount/$scope.pageSize);
			//更新当前页
			$scope.currentPage=page;
			
			//编写请求页码，最大页数需要总页数
			//规则：前五后四
			var begin;
			var end;
			begin = page - 5;
            if (begin < 0) {
                begin = 1;
            }
			
			end = begin+9;
			if(end>$scope.totalPages){
				end=$scope.totalPages;
			}
			begin=end-9;
			 if (begin < 1) {
                begin = 1;
            }
			$scope.pageList=new Array();
			for (var i = begin; i <= end; i++) {
                $scope.pageList.push(i);
           }
			
		
			
		});
	}
	//其他页不会高亮
	$scope.isActivePage = function (page) {
   	 return page == $scope.currentPage;
	}
	// 发起请求 显示第一页数据
    $scope.selectPage($scope.currentPage);
}]);