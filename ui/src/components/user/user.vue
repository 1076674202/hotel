<template>
  <el-card class="box-card">

    <!--面包屑-->
    <!--<el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item>首页</el-breadcrumb-item>
      <el-breadcrumb-item>用户管理</el-breadcrumb-item>
      <el-breadcrumb-item>用户列表</el-breadcrumb-item>
    </el-breadcrumb>-->
    <m-bread level1="用户管理" level2="用户列表"></m-bread>
    <!--搜索-->
    <el-row class="searchRow">
      <el-col>
        <el-input placeholder="请输入内容" v-model="page.query" @clear="reload" clearable class="inputSeach">
          <el-button slot="append" icon="el-icon-search" @click="queryList"></el-button>
        </el-input>
        <el-button type="success" @click="showDia">添加用户</el-button>
      </el-col>
    </el-row>


    <!--表格-->
    <el-table :data="tableData"style="width: 100%" class="Eltable">
      <el-table-column type="index" label="序号"   width="60">   </el-table-column>
      <el-table-column   prop="adminNickname"     label="姓名"  width="180">  </el-table-column>
      <el-table-column   prop="adminGende"  label="性别">

        <template slot-scope="obj">
          {{obj.row.adminGende=='1'? '男':'女'}}

        </template>

      </el-table-column>
      <el-table-column   prop="adminAge"  label="年龄"> </el-table-column>
      <el-table-column   prop="adminMoblie"  label="手机"> </el-table-column>
      <el-table-column   prop="createtime"  label="创建时间">
        <template slot-scope="tableData">
          {{tableData.row.createtime | fmtdate}}
        </template>
      </el-table-column>


     <!-- <el-table-column   prop="address"  label="用户状态">
      </el-table-column>
      -->
      <el-table-column   prop="" label="操作">

        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="open(scope.row.adminId)" icon="el-icon-edit"></el-button>
          <el-button size="mini" type="danger" @click="remove(scope.row.adminId)" icon="el-icon-delete"></el-button>
          <el-button size="mini" type="success" @click="role(scope.row)" icon="el-icon-user"></el-button>
        </template>
      </el-table-column>
    </el-table>


    <!--分页-->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="page.pagenum"
      :page-sizes="[5, 10, 20]"
      :page-size="page.pagesize"
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>


    <!--对话框-->
    <!--添加的对话框-->

    <el-dialog  :title="title" :visible.sync="dialogFormVisible" width="30%">
      <el-form :model="form">
        <el-form-item label="用户名" label-width="80px">
          <el-input v-model="form.adminUsername" autocomplete="off"></el-input>
        </el-form-item>

          <el-form-item label="密码" label-width="80px">
          <el-input v-model="form.adminPassword" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="昵称" label-width="80px">
          <el-input v-model="form.adminNickname" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="手机" label-width="80px">
          <el-input v-model="form.adminMoblie" autocomplete="off"></el-input>
        </el-form-item>
       <!-- <el-form-item label="活动区域" :label-width="formLabelWidth">
          <el-select v-model="form.region" placeholder="请选择活动区域">
            <el-option label="区域一" value="shanghai"></el-option>
            <el-option label="区域二" value="beijing"></el-option>
          </el-select>
        </el-form-item>-->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>


    <!--权限-->

    <el-dialog title="分配角色" :visible.sync="dialogFormRole" width="30%">
      <el-form :model="form">
        <el-form-item label="用户名" label-width="100px">
          {{roleName}}
        </el-form-item>
        <el-form-item label="角色" label-width="100px">
          <el-select v-model="form.adminRole" placeholder="请选择角色">
            <el-option label="请选择" :value="-1"></el-option>
            <el-option v-for="(item,i) in roleList" :key="i" :label="item.roleName" :value="item.roleId"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormRole = false">取 消</el-button>
        <el-button type="primary" @click="setRole">确 定</el-button>
      </div>
    </el-dialog>

  </el-card>
</template>

<script>
    export default {
        data(){
          return{
            title:'',
            tableData :[],
            total:0,
            page:{pagenum:1,pagesize:10,query:''},
            dialogFormVisible:false,
            dialogFormRole:false,

            form:{
                adminId:'',
                adminUsername:'',
                adminPassword:'',
                adminNickname:'',
                adminGende:'',
                adminAge:'',
                adminMoblie:'',
                adminRole:'',
                createman:'',
                modifyman:'',
                createtime:'',
                modifytime:''
            },
              currnadminId:-1,
              currnaroleId:-1,
              roleName:'',
              roleList:[]
          }

        },

        created() {
            this.getList()

        },

        methods: {

            open(id){
                this.title='修改'
                this.$http.get('user/load/'+id)
                    .then(res=>{
                       const {data,msg,code} = res.data;
                       this.dialogFormVisible=true;
                       this.form=data[0];
                }).catch(res => {

                });
            },



            remove(id){
                console.log(id)
                this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$http.delete('/user/remove/'+id).then(res=>{
                        const {msg,code,data} =res.data;
                        if(code ==='ok'){
                            //this.page.pagenum=1;
                            this.getList()
                            this.$message({
                                type: 'success',
                                message: msg
                            });
                        }
                    });
                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });
                });

            },



          /*添加用户*/
            async  save(){
              this.dialogFormVisible=false;
              const res = await this.$http.post('/user/save',this.form);
              const {msg,code,data} =res.data;
                if(code ==='ok'){
                    this.getList()
                    this.form={}
                }else {
                    this.$message.warning(msg)
                }
            },

            success(){
                this.dialogFormRole=true
            },
            role(obj){
                this.currnadminId=obj.adminId
                this.dialogFormRole=true
                this.roleName=obj.adminNickname;
                this.form.adminRole=obj.adminRole
                this.$http.get('/role/select').then(res=>{
                   const {msg,data,code}=res.data
                   this.roleList=data

                });

            },

            setRole(){
                this.$http.get('/user/setrole/'+this.currnadminId+'/'+this.form.adminRole).then(res=>{
                    const {msg,data,code}=res.data
                });
                this.dialogFormRole=false
                this.getList()
            },


          showDia(){
              this.title='添加'
              this.dialogFormVisible=true
              this.form={};
          },

          reload(){
            //重新加载数据
            this.getList();
          },

            handleSizeChange(val) {
                console.log(`每页 ${val} 条`);
                this.page.pagesize=val;
                this.getList()
            },
            handleCurrentChange(val) {
                console.log(`当前页: ${val}`);
                this.page.pagenum=val;
                this.getList()
            },

          queryList(){
            this.getList()
          },

            async getList(){

                const res = await this.$http.post('/user/list',this.page);
                const {msg,code,data} =res.data;
                var obj = data[0];
                console.log(obj)
                if(code ==='ok'){
                  this.tableData=obj.records;
                  this.total=obj.total;
                  //this.$message.success(msg)
                }else {
                  this.$message.warning(msg)
                }
            }


        }
    }
</script>

<style >
.box-card{
  height: 100%;

}
.inputSeach{
  width: 300px;
}

  .searchRow{
    margin-top: 20px;
  }


  .Eltable th{
     padding: 3px 0!important;

  }

.Eltable td{
  padding: 3px 0!important;

}
</style>
